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

package uk.gov.gchq.maestro.commonutil.cache.util;

/**
 * System properties used by the Gaffer cache service implementations.
 */
public final class CacheProperties {

    private CacheProperties() {
        // private constructor to prevent instantiation
    }

    /**
     * Name of the system property to use in order to define the cache service class.
     */
    public static final String CACHE_SERVICE_CLASS = "gaffer.cache.service.class";

    /**
     * Name of the system property to use in order to locate the cache config file.
     */
    public static final String CACHE_CONFIG_FILE = "gaffer.cache.config.file";

}
