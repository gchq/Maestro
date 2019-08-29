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
package uk.gov.gchq.maestro.executor.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.koryphe.util.ReflectionUtil;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiserModules;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.OperationDeclarations;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static com.sun.scenario.Settings.set;

public final class ExecutorPropertiesUtil {
    public static final String ADMIN_AUTH = "maestro.executor.admin.auth";
    public static final String CACHE_CLASS = "maestro.cache.service.class";
    public static final String CONNECT_TIMEOUT = "gaffer.connect-timeout";
    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final String DEFAULT_MAESTRO_CONTEXT_ROOT = "/rest";
    public static final String DEFAULT_MAESTRO_HOST = "localhost";
    public static final int DEFAULT_MAESTRO_PORT = 8080;
    public static final int DEFAULT_READ_TIMEOUT = 10000;
    public static final String EXECUTOR_SERVICE_THREAD_COUNT = "maestro.executor.job.executor.threads";
    public static final String EXECUTOR_SERVICE_THREAD_COUNT_DEFAULT = "50";
    public static final String JOB_TRACKER_ENABLED = "maestro.executor.job.tracker.enabled";
    public static final String JSON_SERIALISER_CLASS = JSONSerialiser.JSON_SERIALISER_CLASS_KEY;
    public static final String JSON_SERIALISER_MODULES = JSONSerialiser.JSON_SERIALISER_MODULES;
    public static final String MAESTRO_CONTEXT_ROOT = "maestro.context-root";
    public static final String MAESTRO_HOST = "maestro.host";
    public static final String MAESTRO_PORT = "maestro.port";
    public static final String OPERATION_DECLARATIONS = "maestro.executor.operation.declarations";
    public static final String READ_TIMEOUT = "maestro.read-timeout";
    /**
     * CSV of extra packages to be included in the reflection scanning.
     */
    public static final String REFLECTION_PACKAGES = "maestro.executor.reflection.packages";
    public static final String STRICT_JSON = JSONSerialiser.STRICT_JSON;
    private static final String MAESTRO_REST_API_VERSION = "v2";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorPropertiesUtil.class);


    private ExecutorPropertiesUtil() {
        // private to prevent this class being instantiated.
        // All methods are static and should be called directly.
    }

    public static Properties loadProperties(final Path propFileLocation) {
        Properties properties = new Properties();
        if (null != propFileLocation) {
            try {
                properties = loadProperties(null != propFileLocation ?
                        Files.newInputStream(propFileLocation) : null);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }

    public static Properties loadProperties(final InputStream propertiesStream) {
        if (null == propertiesStream) {
            return new Properties();
        }
        final Properties props = new Properties();
        try {
            props.load(propertiesStream);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load properties file : " + e.getMessage(), e);
        } finally {
            try {
                propertiesStream.close();
            } catch (final IOException e) {
                LOGGER.error("Failed to close properties stream: {}", e.getMessage(), e);
            }
        }
        return loadProperties(props);
    }

    public static Properties loadProperties(final Properties props) {
        Properties properties = new Properties(); //Todo ?
        properties.putAll(props);
        return properties;
    }

    public static Properties loadProperties(final String pathStr) {
        Properties properties;
        final Path path = Paths.get(pathStr);
        try {
            if (path.toFile().exists()) {
                properties = loadProperties(Files.newInputStream(path));
            } else {
                properties =
                        loadProperties(StreamUtil.openStream(Properties.class,
                                pathStr));
            }
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load properties " +
                    "file : " + e.getMessage(), e);
        }
        return properties;
    }

    public static void merge(final Properties firstProperties,
                             final Properties secondProperties) {
        if (null != firstProperties) {
            firstProperties.putAll(secondProperties);
        }
    }

    /**
     * Returns the operation definitions from the file specified in the
     * properties.
     * This is an optional feature, so if the property does not exist then this
     * function
     * will return an empty object.
     *
     * @param executor to be retrieved from
     * @return The Operation Definitions to load dynamically
     */
    @JsonIgnore
    public static OperationDeclarations getOperationDeclarations(final Executor executor) {
        OperationDeclarations declarations = null;

        final String declarationsPaths = executor.getProperty(OPERATION_DECLARATIONS);
        if (null != declarationsPaths) {
            declarations = OperationDeclarations.fromPaths(declarationsPaths);
        }

        if (null == declarations) {
            declarations = new OperationDeclarations.Builder().build();
        }

        return declarations;
    }

    public static Boolean getJobTrackerEnabled(final Executor executor) {
        return Boolean.valueOf(executor.getPropertyOrDefault(JOB_TRACKER_ENABLED, "false"));
    }

    public static void setJobTrackerEnabled(final Executor executor, final Boolean jobTrackerEnabled) {
        executor.setProperty(JOB_TRACKER_ENABLED, jobTrackerEnabled.toString());
    }

    public static String getReflectionPackages(final Executor executor) {
        return executor.getProperty(REFLECTION_PACKAGES);
    }

    public static String getReflectionPackages(final Config config) {
        return config.getProperty(REFLECTION_PACKAGES);
    }

    public static void setReflectionPackages(final Executor executor, final String packages) {
        executor.setProperty(REFLECTION_PACKAGES, packages);
        ReflectionUtil.addReflectionPackages(packages);
    }

    public static Integer getJobExecutorThreadCount(final Executor executor) {
        return Integer.parseInt(executor.getPropertyOrDefault(EXECUTOR_SERVICE_THREAD_COUNT, EXECUTOR_SERVICE_THREAD_COUNT_DEFAULT));
    }

    public static void addOperationDeclarationPaths(final Executor executor, final String... newPaths) {
        final String newPathsCsv = StringUtils.join(newPaths, ",");
        String combinedPaths = getOperationDeclarationPaths(executor);
        if (null == combinedPaths) {
            combinedPaths = newPathsCsv;
        } else {
            combinedPaths = combinedPaths + "," + newPathsCsv;
        }
        setOperationDeclarationPaths(executor, combinedPaths);
    }

    public static String getOperationDeclarationPaths(final Executor executor) {
        return executor.getProperty(OPERATION_DECLARATIONS);
    }

    public static String getOperationDeclarationPaths(final Config config) {
        return config.getProperty(OPERATION_DECLARATIONS);
    }

    public static void setOperationDeclarationPaths(final Executor executor, final String paths) {
        executor.setProperty(OPERATION_DECLARATIONS, paths);
    }

    public static String getJsonSerialiserClass(final Executor executor) {
        return executor.getProperty(JSON_SERIALISER_CLASS);
    }

    public static String getJsonSerialiserClass(final Config config) {
        return config.getProperty(JSON_SERIALISER_CLASS);
    }

    @JsonIgnore
    public static void setJsonSerialiserClass(final Executor executor, final Class<? extends JSONSerialiser> jsonSerialiserClass) {
        setJsonSerialiserClass(executor, jsonSerialiserClass.getName());
    }

    public static void setJsonSerialiserClass(final Executor executor, final String jsonSerialiserClass) {
        executor.setProperty(JSON_SERIALISER_CLASS, jsonSerialiserClass);
    }

    public static String getJsonSerialiserModules(final Executor executor) {
        return executor.getPropertyOrDefault(JSON_SERIALISER_MODULES, "");
    }

    public static String getJsonSerialiserModules(final Config config) {
        return config.getPropertyOrDefault(JSON_SERIALISER_MODULES, "");
    }

    @JsonIgnore
    public static void setJsonSerialiserModules(final Executor executor, final Set<Class<? extends JSONSerialiserModules>> modules) {
        final Set<String> moduleNames = new HashSet<>(modules.size());
        for (final Class module : modules) {
            moduleNames.add(module.getName());
        }
        setJsonSerialiserModules(executor, StringUtils.join(moduleNames, ","));
    }

    public static void setJsonSerialiserModules(final Executor executor, final String modules) {
        executor.setProperty(JSON_SERIALISER_MODULES, modules);
    }

    public static Boolean getStrictJson(final Executor executor) {
        final String strictJson = executor.getProperty(STRICT_JSON);
        return null == strictJson ? null : Boolean.parseBoolean(strictJson);
    }

    public static Boolean getStrictJson(final Config con) {
        final String strictJson = con.getProperty(STRICT_JSON);
        return null == strictJson ? null : Boolean.parseBoolean(strictJson);
    }

    public static void setStrictJson(final Executor executor, final Boolean strictJson) {
        executor.setProperty(STRICT_JSON, null == strictJson ? null : Boolean.toString(strictJson));
    }

    public static String getAdminAuth(final Executor executor) {
        return executor.getPropertyOrDefault(ADMIN_AUTH, "");
    }

    public static void setAdminAuth(final Executor executor, final String adminAuth) {
        executor.getPropertyOrDefault(ADMIN_AUTH, adminAuth);
    }

    public static void setCacheClass(final Executor executor, final String cacheClass) {
        executor.setProperty(CACHE_CLASS, cacheClass);
    }

    public static void getCacheClass(final Executor executor) {
        executor.getProperty(CACHE_CLASS);
    }

    public static URL getMaestroUrl(final Executor executor) {
        return getMaestroUrl(executor, null);
    }

    public static URL getMaestroUrl(final Executor executor, final String suffix) {
        return getMaestroUrl(executor, "http", suffix);
    }

    public static URL getMaestroUrl(final Executor executor, final String protocol, final String suffix) {
        final String urlSuffix;
        if (StringUtils.isNotEmpty(suffix)) {
            urlSuffix = prepend("/", suffix);
        } else {
            urlSuffix = "";
        }

        try {
            String contextRoot = prepend("/", getGafferContextRoot(executor));
            contextRoot = addSuffix("/", contextRoot) + MAESTRO_REST_API_VERSION;
            return new URL(protocol, getMaestroHost(executor), getMaestroPort(executor),
                    contextRoot + urlSuffix);
        } catch (final MalformedURLException e) {
            throw new IllegalArgumentException("Could not create Gaffer URL from host (" + getMaestroHost(executor)
                    + "), port (" + getMaestroPort(executor)
                    + ") and context root (" + getGafferContextRoot(executor) + ")", e);
        }
    }

    protected static String prepend(final String prefix, final String string) {
        if (!string.startsWith(prefix)) {
            return prefix + string;
        }

        return string;
    }

    public static String getGafferContextRoot(final Executor executor) {
        return (String) executor.config.getPropertyOrDefault(MAESTRO_CONTEXT_ROOT, DEFAULT_MAESTRO_CONTEXT_ROOT);
    }

    protected static String addSuffix(final String suffix, final String string) {
        if (!string.endsWith(suffix)) {
            return string + suffix;
        }

        return string;
    }

    public static String getMaestroHost(final Executor executor) {
        return (String) executor.config.getPropertyOrDefault(MAESTRO_HOST, DEFAULT_MAESTRO_HOST);
    }

    public static int getMaestroPort(final Executor executor) {
        final String portStr = (String) executor.config.getPropertyOrDefault(MAESTRO_PORT, null);
        try {
            return null == portStr ? DEFAULT_MAESTRO_PORT : Integer.parseInt(portStr);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Unable to convert gaffer port into an integer", e);
        }
    }

    public static void setMeastroHost(final String gafferHost) {
        set(MAESTRO_HOST, gafferHost);
    }

    public static String getGafferHost(final Executor executor) {
        return (String) executor.config.getPropertyOrDefault(MAESTRO_HOST, DEFAULT_MAESTRO_HOST);
    }

    public static void setGafferPort(final int gafferPort) {
        set(MAESTRO_PORT, String.valueOf(gafferPort));
    }

    public static int getConnectTimeout(final Executor executor) {
        final String timeout = (String) executor.config.getPropertyOrDefault(CONNECT_TIMEOUT, null);
        try {
            return null == timeout ? DEFAULT_CONNECT_TIMEOUT : Integer.parseInt(timeout);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Unable to convert gaffer timeout into an integer", e);
        }
    }

    public static void setConnectTimeout(final Executor executor, final int timeout) {
        executor.setProperty(CONNECT_TIMEOUT, String.valueOf(timeout));
    }

    public static int getReadTimeout(final Executor executor) {
        final String timeout = (String) executor.config.getPropertyOrDefault(READ_TIMEOUT, null);
        try {
            return null == timeout ? DEFAULT_READ_TIMEOUT : Integer.parseInt(timeout);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Unable to convert gaffer timeout into an integer", e);
        }
    }

    public static void setReadTimeout(final Executor executor, final int timeout) {
        executor.setProperty(READ_TIMEOUT, String.valueOf(timeout));
    }
}
