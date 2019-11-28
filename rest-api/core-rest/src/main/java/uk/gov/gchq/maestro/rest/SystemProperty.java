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

package uk.gov.gchq.maestro.rest;

import uk.gov.gchq.maestro.commonutil.DebugUtil;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.executor.util.ExecutorPropertiesUtil;
import uk.gov.gchq.maestro.rest.factory.DefaultExecutorFactory;
import uk.gov.gchq.maestro.rest.factory.UnknownUserFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * System property keys and default values.
 */
public abstract class SystemProperty {
    // KEYS
    public static final String EXECUTOR_CONFIG_PATH = "maestro.executor.config";
    public static final String MAESTRO_PROPERTIES_PATH = "maestro.properties";
    public static final String MAESTRO_VERSION = "maestro.version";
    public static final String KORYPHE_VERSION = "koryphe.version";
    public static final String BASE_PATH = "maestro.rest-api.basePath";
    public static final String REST_API_VERSION = "maestro.rest-api.version";
    public static final String EXECUTOR_FACTORY_CLASS = "maestro.executor.factory.class";
    public static final String USER_FACTORY_CLASS = "maestro.user.factory.class";
    public static final String SERVICES_PACKAGE_PREFIX = "maestro.rest-api.resourcePackage";
    public static final String PACKAGE_PREFIXES = "maestro.package.prefixes";
    public static final String JSON_SERIALISER_CLASS = JSONSerialiser.JSON_SERIALISER_CLASS_KEY;
    public static final String JSON_SERIALISER_MODULES = JSONSerialiser.JSON_SERIALISER_MODULES;
    public static final String REST_DEBUG = DebugUtil.DEBUG;

    // Exposed Property Keys
    /**
     * A CSV of properties to expose via the properties endpoint.
     */
    public static final String EXPOSED_PROPERTIES = "maestro.properties";
    public static final String APP_TITLE = "maestro.properties.app.title";
    public static final String APP_DESCRIPTION = "maestro.properties.app.description";
    public static final String APP_BANNER_COLOUR = "maestro.properties.app.banner.colour";
    public static final String APP_BANNER_DESCRIPTION = "maestro.properties.app.banner.description";
    public static final String APP_DOCUMENTATION_URL = "maestro.properties.app.doc.url";
    public static final String LOGO_LINK = "maestro.properties.app.logo.link";
    public static final String LOGO_IMAGE_URL = "maestro.properties.app.logo.src";
    public static final String FAVICON_SMALL_URL = "maestro.properties.app.logo.favicon.small";
    public static final String FAVICON_LARGE_URL = "maestro.properties.app.logo.favicon.large";


    /**
     * @deprecated create a Config json file and use EXECUTOR_CONFIG_PATH instead
     */
    @Deprecated
    public static final String EXECUTOR_LIBRARY_CONFIG = "maestro.executor.library.config";

    // DEFAULTS
    /**
     * Comma separated list of package prefixes to search for Functions and {@link uk.gov.gchq.maestro.operation.Operation}s.
     */
    public static final String PACKAGE_PREFIXES_DEFAULT = "uk.gov.gchq";
    public static final String SERVICES_PACKAGE_PREFIX_DEFAULT = "uk.gov.gchq.maestro.rest";
    public static final String BASE_PATH_DEFAULT = "rest";
    public static final String CORE_VERSION = "2.0.0";
    public static final String MAESTRO_VERSION_DEFAULT = getVersion(MAESTRO_VERSION);
    public static final String KORYPHE_VERSION_DEFAULT = getVersion(KORYPHE_VERSION);
    public static final String EXECUTOR_FACTORY_CLASS_DEFAULT = DefaultExecutorFactory.class.getName();
    public static final String USER_FACTORY_CLASS_DEFAULT = UnknownUserFactory.class.getName();
    public static final String REST_DEBUG_DEFAULT = DebugUtil.DEBUG_DEFAULT;
    public static final String APP_TITLE_DEFAULT = "Maestro REST";
    public static final String APP_DESCRIPTION_DEFAULT = "The Maestro REST service.";
    public static final String APP_DOCUMENTATION_URL_DEFAULT = "https://gchq.github.io/maestro-doc/";
    public static final String LOGO_LINK_DEFAULT = "https://github.com/gchq/Maestro";
    public static final String LOGO_IMAGE_URL_DEFAULT = "images/logo.png";

    private static Map<String, Object> versionProperties;

    private SystemProperty() {
        // Private constructor to prevent instantiation.
    }

    private static String getVersion(final String propertyKey) {
        if (versionProperties == null) {
            loadVersionProperties();
        }
        return (String) versionProperties.get(propertyKey);
    }

    private static void loadVersionProperties() {
        try {
            InputStream input = StreamUtil.openStream(SystemProperty.class, "version.properties");
            versionProperties = new HashMap<>(ExecutorPropertiesUtil.loadProperties(input));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
