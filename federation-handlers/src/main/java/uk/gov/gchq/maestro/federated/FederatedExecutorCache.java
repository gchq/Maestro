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

import uk.gov.gchq.maestro.commonutil.Pair;
import uk.gov.gchq.maestro.commonutil.cache.Cache;
import uk.gov.gchq.maestro.commonutil.exception.CacheOperationException;
import uk.gov.gchq.maestro.executor.Executor;

import java.util.Set;

import static java.util.Objects.isNull;

/**
 * Wrapper around the {@link uk.gov.gchq.maestro.commonutil.cache.CacheServiceLoader} to provide an interface for
 * handling the executors within a federated Executor.
 */
public class FederatedExecutorCache extends Cache<Pair<Executor, FederatedAccess>> {
    public static final String ERROR_ADDING_GRAPH_TO_CACHE_GRAPH_ID_S = "Error adding graph to cache. graphId: %s";

    public FederatedExecutorCache() {
        super("federatedExecutorCache");
    }

    /**
     * Get all the ID's related to the executors stored in the cache.
     *
     * @return all the Graph ID's within the cache as unmodifiable set.
     */
    public Set<String> getAllExecutorIds() {
        return super.getAllKeys();
    }

    public void addExecutorToCache(final Executor executor, final FederatedAccess access, final boolean overwrite) throws CacheOperationException {
        String id = executor.getId();
        Pair<Executor, FederatedAccess> pair = new Pair<>(executor, access);
        try {
            addToCache(id, pair, overwrite);
        } catch (final CacheOperationException e) {
            throw new CacheOperationException(String.format(ERROR_ADDING_GRAPH_TO_CACHE_GRAPH_ID_S, id), e.getCause());
        }
    }

    public Executor getExecutorFromCache(final String graphId) {
        final Pair<Executor, FederatedAccess> fromCache = getFromCache(graphId);
        return (isNull(fromCache)) ? null : fromCache.getFirst();
    }

    public FederatedAccess getAccessFromCache(final String graphId) {
        final Pair<Executor, FederatedAccess> fromCache = getFromCache(graphId);
        return fromCache.getSecond();
    }
}
