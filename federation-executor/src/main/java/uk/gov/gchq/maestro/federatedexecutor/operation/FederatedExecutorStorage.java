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

package uk.gov.gchq.maestro.federatedexecutor.operation;

import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.cache.CacheServiceLoader;
import uk.gov.gchq.maestro.commonutil.exception.CacheOperationException;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OverwritingException;
import uk.gov.gchq.maestro.user.User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FederatedExecutorStorage {
    public static final boolean DEFAULT_DISABLED_BY_DEFAULT = false;
    public static final String ERROR_ADDING_GRAPH_TO_CACHE = "Error adding executor, ExecutorId is known within the cache, but %s is different. ExecutorId: %s";
    public static final String USER_IS_ATTEMPTING_TO_OVERWRITE = "User is attempting to overwrite a executor within FederatedStore. ExecutorId: %s";
    public static final String ACCESS_IS_NULL = "Can not put executor into storage without a FederatedAccess key.";
    public static final String GRAPH_IDS_NOT_VISIBLE = "The following executorIds are not visible or do not exist: %s";
    public static final String UNABLE_TO_MERGE_THE_SCHEMAS_FOR_ALL_OF_YOUR_FEDERATED_GRAPHS = "Unable to merge the schemas for all of your federated executors: %s. You can limit which executors to query for using the operation option: %s";
    private Map<FederatedAccess, Set<Executor>> storage = new HashMap<>();
    private FederatedExecutorCache federatedStoreCache = new FederatedExecutorCache();
    private Boolean isCacheEnabled = false;
    // private ExecutorLibrary executorLibrary; TODO

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

    /**
     * places a executor into storage, protected by the given access.
     * <p> ExecutorId can't already exist, otherwise {@link
     * uk.gov.gchq.maestro.commonutil.exception.OverwritingException} is thrown.
     * <p> Access can't be null otherwise {@link IllegalArgumentException} is
     * thrown
     *
     * @param executor the executor to add to the storage.
     * @param access   access required to for the executor.
     * @throws MaestroCheckedException if unable to put arguments into storage
     */
    public void put(final Executor executor, final FederatedAccess access) throws MaestroCheckedException {
        if (executor != null) {
            String executorId = executor.getConfig().getId();
            try {
                if (null == access) {
                    throw new IllegalArgumentException(ACCESS_IS_NULL);
                }

                // if (null != executorLibrary) {
                //     executorLibrary.checkExisting(executorId, executor.getDeserialisedSchema(), executor.getDeserialisedProperties());
                // }

                validateExisting(executor);
                final Executor builtExecutor = executor;
                if (isCacheEnabled()) {
                    addToCache(builtExecutor, access);
                }

                Set<Executor> existingExecutors = storage.get(access);
                if (null == existingExecutors) {
                    existingExecutors = Sets.newHashSet(builtExecutor);
                    storage.put(access, existingExecutors);
                } else {
                    existingExecutors.add(builtExecutor);
                }
            } catch (final Exception e) {
                throw new MaestroCheckedException("Error adding executor " + executorId + " to storage due to: " + e.getMessage(), e);
            }
        } else {
            throw new MaestroCheckedException("Executor cannot be null");
        }
    }


    /**
     * Returns all the executorIds that are visible for the given user.
     *
     * @param user to match visibility against.
     * @return visible executorIds.
     */
    public Collection<String> getAllIds(final User user) {
        final Set<String> rtn = getAllStream(user)
                .map(e -> e.getConfig().getId())
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
        for (final Map.Entry<FederatedAccess, Set<Executor>> entry : storage.entrySet()) {
            if (isValidToView(user, entry.getKey())) {
                final Set<Executor> executors = entry.getValue();
                if (null != executors) {
                    HashSet<Executor> remove = Sets.newHashSet();
                    for (final Executor executor : executors) {
                        if (executor.getConfig().getId().equals(executorId)) {
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

    /**
     * returns all executors objects matching the given executorIds, that is visible
     * to the user.
     *
     * @param user        to match visibility against.
     * @param executorIds the executorIds to get executors for.
     * @return visible executors from the given executorIds.
     */
    public Collection<Executor> get(final User user, final List<String> executorIds) {
        if (null == user) {
            return Collections.emptyList();
        }

        validateAllGivenExecutorIdsAreVisibleForUser(user, executorIds);
        Stream<Executor> executors = getStream(user, executorIds);
        if (null != executorIds) {
            executors = executors.sorted((g1, g2) -> executorIds.indexOf(g1.getConfig().getId()) - executorIds.indexOf(g2.getConfig().getId()));
        }
        final Set<Executor> rtn = executors.collect(Collectors.toCollection(LinkedHashSet::new));
        return Collections.unmodifiableCollection(rtn);
    }


    private void validateAllGivenExecutorIdsAreVisibleForUser(final User user, final Collection<String> executorIds) {
        if (null != executorIds) {
            final Collection<String> visibleIds = getAllIds(user);
            if (!visibleIds.containsAll(executorIds)) {
                final Set<String> notVisibleIds = Sets.newHashSet(executorIds);
                notVisibleIds.removeAll(visibleIds);
                throw new IllegalArgumentException(String.format(GRAPH_IDS_NOT_VISIBLE, notVisibleIds));
            }
        }
    }

    private void validateExisting(final Executor executor) {
        final String executorId = executor.getConfig().getId();
        for (final Set<Executor> executors : storage.values()) {
            for (final Executor g : executors) {
                if (g.getConfig().getId().equals(executorId)) {
                    throw new OverwritingException((String.format(USER_IS_ATTEMPTING_TO_OVERWRITE, executorId)));
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
                .filter(executor -> executorIds.contains(executor.getConfig().getId()));
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

    private void addToCache(final Executor newExecutor, final FederatedAccess access) {
        final String executorId = newExecutor.getConfig().getId();
        if (federatedStoreCache.contains(executorId)) {
            validateSameAsFromCache(newExecutor, executorId);
        } else {
            try {
                federatedStoreCache.addExecutorToCache(newExecutor, access, false);
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
        // if (!newExecutor.getConfig().getProperties().getProperties().equals(fromCache.getConfig().getProperties().getProperties())) {
        //     throw new RuntimeException(String.format(ERROR_ADDING_GRAPH_TO_CACHE, ExecutorConfigEnum.PROPERTIES.toString(), executorId));
        // } else {
        //     if (!JsonUtil.equals(newExecutor.getSchema().toJson(false), fromCache.getSchema().toJson(false))) {
        //         throw new RuntimeException(String.format(ERROR_ADDING_GRAPH_TO_CACHE, ExecutorConfigEnum.SCHEMA.toString(), executorId));
        //     } else {
        //         if (!newExecutor.getConfig().getId().equals(fromCache.getConfig().getId())) {
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
}
