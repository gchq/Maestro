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

package uk.gov.gchq.maestro.commonutil.cache.impl;

import uk.gov.gchq.maestro.commonutil.cache.ICache;
import uk.gov.gchq.maestro.commonutil.cache.ICacheService;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple impl of the {@link ICacheService} interface which uses a
 * {@link HashMapCache} as the cache impl.
 */
public class HashMapCacheService implements ICacheService {
    public static final String STATIC_CACHE = "maestro.cache.hashmap.static";
    public static final String JAVA_SERIALISATION_CACHE = "maestro.cache.hashmap.useJavaSerialisation";
    private static final HashMap<String, HashMapCache> STATIC_CACHES = new HashMap<>();
    private final HashMap<String, HashMapCache> nonStaticCaches = new HashMap<>();
    private boolean useJavaSerialisation = false;

    private HashMap<String, HashMapCache> caches = nonStaticCaches;

    @Override
    public void initialise(final Map<String, Object> properties) {
        if (properties != null) {
            useJavaSerialisation = (boolean) properties.getOrDefault(JAVA_SERIALISATION_CACHE, false);
        }

        if (properties != null && (boolean) properties.getOrDefault(STATIC_CACHE, false)) {
            caches = STATIC_CACHES;
        } else {
            caches = nonStaticCaches;
        }
    }

    @Override
    public void shutdown() {
        caches.clear();
    }

    @Override
    public <K, V> ICache<K, V> getCache(final String cacheName) {
        HashMapCache<K, V> cache = caches.computeIfAbsent(cacheName, k -> new HashMapCache<>(useJavaSerialisation));

        return cache;
    }
}
