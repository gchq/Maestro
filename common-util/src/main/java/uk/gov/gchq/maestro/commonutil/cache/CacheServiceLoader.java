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

package uk.gov.gchq.maestro.commonutil.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.commonutil.cache.util.CacheProperties;

import java.util.Map;

/**
 * Initialised when the executor is initialised. Looks at a system property to
 * determine the cache service to load.
 * Then initialises it, after which any component may use {@code CacheServiceLoader.getService()} to get the service
 * that can retrieve the appropriate cache.
 */
public final class CacheServiceLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheServiceLoader.class);
    private static ICacheService service;
    private static boolean shutdownHookAdded = false;

    /**
     * Looks at a system property and initialises an appropriate cache service. Adds a shutdown hook
     * which gracefully closes the cache service if JVM is stopped. This should not be relied upon
     * in a servlet context - use the ServletLifecycleListener located in the REST module instead
     *
     * @param properties the cache service properties
     * @throws IllegalArgumentException if an invalid cache class is specified in the system property
     */
    public static void initialise(final Map<String, Object> properties) {
        if (null == properties) {
            LOGGER.warn("received null properties - exiting initialise method without creating service");
            return;
        }
        final String cacheClass = (String) properties.get(CacheProperties.CACHE_SERVICE_CLASS); ////TODO get class

        if (null == cacheClass) {
            if (null == service) {
                LOGGER.debug("No cache service class was specified in properties.");
            }
            return;
        }
        try {
            service = Class.forName(cacheClass).asSubclass(ICacheService.class).newInstance();

        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to instantiate cache using class " + cacheClass, e);
        }

        service.initialise(properties);

        if (!shutdownHookAdded) {
            Runtime.getRuntime().addShutdownHook(new Thread(CacheServiceLoader::shutdown));
            shutdownHookAdded = true;
        }
    }

    /**
     * Get the cache service object.
     *
     * @return the cache service
     */
    public static ICacheService getService() {
        return service;
    }

    /**
     * @return true if the cache is enabled
     */
    public static boolean isEnabled() {
        return null != service;
    }

    /**
     * Gracefully shutdown and reset the cache service.
     */
    public static void shutdown() {
        if (null != service) {
            service.shutdown();
        }

        service = null;
    }

    private CacheServiceLoader() {
        // private constructor to prevent instantiation
    }
}
