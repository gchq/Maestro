/*
 * Copyright 2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.maestro.federated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import uk.gov.gchq.maestro.commonutil.cache.CacheServiceLoader;
import uk.gov.gchq.maestro.commonutil.exception.CacheOperationException;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OverwritingException;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.operation.user.User;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@JsonPropertyOrder(value = {"class", "storage"}, alphabetic = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class FederatedExecutorStorage implements Serializable {
    public static final boolean DEFAULT_DISABLED_BY_DEFAULT = false;
    public static final String ERROR_ADDING_GRAPH_TO_CACHE = "Error adding executor, ExecutorId is known within the cache, but %s is different. ExecutorId: %s";
    public static final String USER_IS_ATTEMPTING_TO_OVERWRITE = "User is attempting to overwrite a executor within FederatedStore. ExecutorId: %s";
    public static final String ACCESS_IS_NULL = "Can not put executor into storage without a FederatedAccess key.";
    public static final String GRAPH_IDS_NOT_VISIBLE = "The following executorIds are not visible or do not exist: %s";
    public static final String UNABLE_TO_MERGE_THE_SCHEMAS_FOR_ALL_OF_YOUR_FEDERATED_GRAPHS = "Unable to merge the schemas for all of your federated executors: %s. You can limit which executors to query for using the operation option: %s";
    public static final String ERROR_GETTING_S_FROM_FEDERATED_EXECUTOR_STORAGE_S = "Error getting: %s from FederatedExecutorStorage -> %s";
    private static final long serialVersionUID = -306891755744655032L;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
    @JsonSerialize(keyUsing = MapStorageSerialiser.class)
    @JsonDeserialize(keyUsing = MapStorageDeserialiser.class)
    private final TreeMap<FederatedAccess, TreeSet<Executor>> storage = new TreeMap<>(); //TODO set might need to be ordered for serialisation tests
    private FederatedExecutorCache federatedStoreCache = new FederatedExecutorCache();
    private Boolean isCacheEnabled = false;
    // private ExecutorLibrary executorLibrary; TODO


    public FederatedExecutorStorage() {
    }

    @JsonCreator
    public FederatedExecutorStorage(@JsonProperty("storage") final Map<FederatedAccess, TreeSet<Executor>> storage) {
        this.storage.putAll(storage);
    }

    @JsonGetter("storage")
    public Map<FederatedAccess, TreeSet<Executor>> getStorage() {
        // return Collections.unmodifiableMap(storage);
        return storage;
    }

    protected void startCacheServiceLoader() throws MaestroCheckedException {
        if (CacheServiceLoader.isEnabled()) {
            isCacheEnabled = true;
            makeAllExecutorsFromCache();
        }
    }

    public void put(final Collection<Executor> executors, final FederatedAccess access) throws MaestroCheckedException {
        for (final Executor executor : executors) {
            put(executor, access);
        }
    }

    public FederatedExecutorStorage put(final Executor executor, final FederatedAccess access) throws MaestroCheckedException {
        if (nonNull(executor)) {
            try {
                if (isNull(access)) {
                    throw new IllegalArgumentException(ACCESS_IS_NULL);
                }

                // if (null != executorLibrary) {
                //     executorLibrary.checkExisting(executorId, executor.getDeserialisedSchema(), executor.getDeserialisedProperties());
                // }

                validateExisting(executor);
                if (isCacheEnabled()) {
                    addToCache(executor, access);
                }

                TreeSet<Executor> existingExecutors = storage.get(access);
                if (null == existingExecutors) {
                    existingExecutors = new TreeSet<>();
                    existingExecutors.add(executor);
                    storage.put(access, existingExecutors);
                } else {
                    existingExecutors.add(executor);
                }
            } catch (final Exception e) {
                throw new MaestroCheckedException("Error adding executor id: " + executor.getId() + " to storage", e);
            }
        } else {
            throw new MaestroCheckedException("Executor cannot be null");
        }
        return this;
    }


    /**
     * Returns all the executorIds that are visible for the given user.
     *
     * @param user to match visibility against.
     * @return visible executorIds.
     */
    public Collection<String> getAllIds(final User user) {
        final Set<String> rtn = getAllStream(user)
                .map(Executor::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return Collections.unmodifiableSet(rtn);
    }

    /**
     * Returns all executor object that are visible for the given user.
     *
     * @param user to match visibility against.
     * @return visible executors
     */
    public Collection<Executor> getAll(final User user) {
        final Set<Executor> rtn = getAllStream(user)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return Collections.unmodifiableCollection(rtn);
    }

    /**
     * Removes a executor from storage and returns the success. The given user
     * must
     * have visibility of the executor to be able to remove it.
     *
     * @param executorId the executorId to remove.
     * @param user       to match visibility against.
     * @return if a executor was removed.
     * @see #isValidToView(User, FederatedAccess)
     */
    public boolean remove(final String executorId, final User user) {
        boolean isRemoved = false;
        for (final Map.Entry<FederatedAccess, TreeSet<Executor>> entry : storage.entrySet()) {
            if (isValidToView(user, entry.getKey())) {
                final Set<Executor> executors = entry.getValue();
                if (null != executors) {
                    HashSet<Executor> remove = Sets.newHashSet();
                    for (final Executor executor : executors) {
                        if (executor.getId().equals(executorId)) {
                            remove.add(executor);
                            deleteFromCache(executorId);
                            isRemoved = true;
                        }
                    }
                    executors.removeAll(remove);
                }
            }
        }
        return isRemoved;
    }

    private void deleteFromCache(final String executorId) {
        if (isCacheEnabled()) {
            federatedStoreCache.deleteFromCache(executorId);
        }
    }

    public Collection<Executor> get(final User user, final List<String> executorIds) throws MaestroCheckedException {
        Objects.requireNonNull(executorIds, "Can't get Executors with null ids");
        if (null == user) {
            return Collections.emptyList();
        }

        try {
            validateAllGivenExecutorIdsAreVisibleForUser(user, executorIds);
        } catch (final MaestroCheckedException e) {
            throw new MaestroCheckedException(String.format(ERROR_GETTING_S_FROM_FEDERATED_EXECUTOR_STORAGE_S, executorIds.toString(), e.getMessage()), e);
        }
        Stream<Executor> configs = getStream(user, executorIds);
        if (null != executorIds) {
            configs = configs.sorted(Comparator.comparingInt(g -> executorIds.indexOf(g.getId())));
        }
        final LinkedHashSet<Executor> rtn = configs.collect(Collectors.toCollection(LinkedHashSet::new));
        return Collections.unmodifiableSet(rtn);
    }


    private void validateAllGivenExecutorIdsAreVisibleForUser(final User user, final Collection<String> executorIds) throws MaestroCheckedException {
        if (null != executorIds) {
            final Collection<String> visibleIds = getAllIds(user);
            if (!visibleIds.containsAll(executorIds)) {
                final Set<String> notVisibleIds = Sets.newHashSet(executorIds);
                notVisibleIds.removeAll(visibleIds);
                final IllegalArgumentException e = new IllegalArgumentException(String.format(GRAPH_IDS_NOT_VISIBLE, notVisibleIds));
                throw new MaestroCheckedException(e.getMessage(), e);
            }
        }
    }

    private void validateExisting(final Executor addingConfig) {
        final String addingConfigId = addingConfig.getId();
        for (final Set<Executor> executors : storage.values()) {
            for (final Executor e : executors) {
                if (e.getId().equals(addingConfigId)) {
                    throw new OverwritingException((String.format(USER_IS_ATTEMPTING_TO_OVERWRITE, addingConfigId)));
                }
            }
        }
    }

    /**
     * @param user   to match visibility against, if null will default to
     *               false/denied
     *               access
     * @param access access the user must match.
     * @return the boolean access
     */
    private boolean isValidToView(final User user, final FederatedAccess access) {
        return null != access && access.isValidToExecute(user);
    }

    /**
     * @param user        to match visibility against.
     * @param executorIds filter on executorIds
     * @return a stream of executors for the given executorIds and the user has visibility for.
     * If executorIds is null then only enabled by default executors are returned that the user can see.
     */
    private Stream<Executor> getStream(final User user, final Collection<String> executorIds) {
        if (null == executorIds) {
            return storage.entrySet()
                    .stream()
                    .filter(entry -> isValidToView(user, entry.getKey()))
                    .filter(entry -> !entry.getKey().isDisabledByDefault())
                    .flatMap(entry -> entry.getValue().stream());
        }

        return storage.entrySet()
                .stream()
                .filter(entry -> isValidToView(user, entry.getKey()))
                .flatMap(entry -> entry.getValue().stream())
                .filter(config -> executorIds.contains(config.getId()));
    }

    /**
     * @param user to match visibility against
     * @return executors that are enabled by default and the user has visibility of.
     */
    private Stream<Executor> getStream(final User user) {
        return getStream(user, null);
    }

    /**
     * @param user to match visibility against.
     * @return a stream of executors the user has visibility for.
     */
    private Stream<Executor> getAllStream(final User user) {
        return storage.entrySet()
                .stream()
                .filter(entry -> isValidToView(user, entry.getKey()))
                .flatMap(entry -> entry.getValue().stream());
    }

    private void addToCache(final Executor executor, final FederatedAccess access) {
        final String executorId = executor.getId();
        if (federatedStoreCache.contains(executorId)) {
            validateSameAsFromCache(executor, executorId);
        } else {
            try {
                federatedStoreCache.addExecutorToCache(executor, access, false);
            } catch (final OverwritingException e) {
                throw new OverwritingException((String.format("User is attempting to overwrite a executor within the cacheService. ExecutorId: %s", executorId)));
            } catch (final CacheOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void validateSameAsFromCache(final Executor newExecutor, final String executorId) {
        //TODO
        // final Executor fromCache = federatedStoreCache.getExecutorFromCache(executorId).getExecutor(executorLibrary);
        // if (!newExecutor.getExecutor().getProperties().getProperties().equals(fromCache.getExecutor().getProperties().getProperties())) {
        //     throw new RuntimeException(String.format(ERROR_ADDING_GRAPH_TO_CACHE, ExecutorConfigEnum.PROPERTIES.toString(), executorId));
        // } else {
        //     if (!JsonUtil.equals(newExecutor.getSchema().toJson(false), fromCache.getSchema().toJson(false))) {
        //         throw new RuntimeException(String.format(ERROR_ADDING_GRAPH_TO_CACHE, ExecutorConfigEnum.SCHEMA.toString(), executorId));
        //     } else {
        //         if (!newExecutor.getExecutor().getId().equals(fromCache.getExecutor().getId())) {
        //             throw new RuntimeException(String.format(ERROR_ADDING_GRAPH_TO_CACHE, "ExecutorId", executorId));
        //         }
        //     }
        // }
    }

    // public void setExecutorLibrary(final ExecutorLibrary executorLibrary) { TODO
    //     this.executorLibrary = executorLibrary;
    // }

    /**
     * Enum for the Executor Properties or Schema
     */
    public enum ExecutorConfigEnum {
        SCHEMA("schema"), PROPERTIES("properties");

        private final String value;

        ExecutorConfigEnum(final String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    private Boolean isCacheEnabled() {
        boolean rtn = false;
        if (isCacheEnabled) {
            if (federatedStoreCache.getCache() == null) {
                throw new RuntimeException("No cache has been set, please initialise the FederatedStore instance");
            }
            rtn = true;
        }
        return rtn;
    }

    private void makeExecutorFromCache(final String executorId) throws MaestroCheckedException {
        final Executor executor = federatedStoreCache.getExecutorFromCache(executorId);
        final FederatedAccess accessFromCache = federatedStoreCache.getAccessFromCache(executorId);
        put(executor, accessFromCache);
    }

    private void makeAllExecutorsFromCache() throws MaestroCheckedException {
        final Set<String> allExecutorIds = federatedStoreCache.getAllExecutorIds();
        for (final String executorId : allExecutorIds) {
            makeExecutorFromCache(executorId);
        }
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FederatedExecutorStorage that = (FederatedExecutorStorage) o;

        final EqualsBuilder eb = new EqualsBuilder();

        // eb.append(this.storage, that.storage);

        if (eb.append(this.storage.size(), that.storage.size()).isEquals()) {
            for (final FederatedAccess thisAccess : storage.keySet()) {
                if (!eb.append(true, that.storage.containsKey(thisAccess)).isEquals()) {
                    break;
                }

                final TreeSet<Executor> thisValue = storage.get(thisAccess);
                final TreeSet<Executor> thatValue = that.storage.get(thisAccess);

                if (!eb.append(thisValue.size(), thatValue.size()).isEquals()) {
                    break;
                }
                for (final Executor thisExecutor : thisValue) {
                    if (!eb.append(true, thatValue.contains(thisExecutor)).isEquals()) {
                        break;
                    }
                }
            }
        }

        return eb.append(isCacheEnabled, that.isCacheEnabled)
                // .append(federatedStoreCache, that.federatedStoreCache) TODO Examine if/when this is required/used
                .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(storage)
                // .append(federatedStoreCache) TODO Examine if/when this is required/used
                .append(isCacheEnabled)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("storage", storage)
                // .append("federatedStoreCache", federatedStoreCache)  TODO Examine if/when this is required/used
                .append("isCacheEnabled", isCacheEnabled)
                .toString();
    }
}
