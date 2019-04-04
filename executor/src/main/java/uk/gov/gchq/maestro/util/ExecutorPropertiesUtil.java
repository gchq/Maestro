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
package uk.gov.gchq.maestro.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import uk.gov.gchq.koryphe.util.ReflectionUtil;
import uk.gov.gchq.maestro.ExecutorProperties;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiserModules;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclarations;

import java.util.HashSet;
import java.util.Set;

public final class ExecutorPropertiesUtil {
    public static final String OPERATION_DECLARATIONS = "maestro.executor.operation.declarations";

    public static final String JOB_TRACKER_ENABLED = "maestro.executor.job.tracker.enabled";

    public static final String EXECUTOR_SERVICE_THREAD_COUNT = "maestro.executor.job.executor.threads";
    public static final String EXECUTOR_SERVICE_THREAD_COUNT_DEFAULT = "50";

    public static final String JSON_SERIALISER_CLASS = JSONSerialiser.JSON_SERIALISER_CLASS_KEY;
    public static final String JSON_SERIALISER_MODULES = JSONSerialiser.JSON_SERIALISER_MODULES;
    public static final String STRICT_JSON = JSONSerialiser.STRICT_JSON;

    public static final String ADMIN_AUTH = "maestro.executor.admin.auth";

    public static final String CACHE_CLASS = "maestro.cache.service.class";

    /**
     * CSV of extra packages to be included in the reflection scanning.
     */
    public static final String REFLECTION_PACKAGES = "maestro.executor.reflection.packages";

    private ExecutorPropertiesUtil() {
        // private to prevent this class being instantiated.
        // All methods are static and should be called directly.
    }

    /**
     * Returns the operation definitions from the file specified in the
     * properties.
     * This is an optional feature, so if the property does not exist then this
     * function
     * will return an empty object.
     *
     * @param properties to be retrieved from
     * @return The Operation Definitions to load dynamically
     */
    @JsonIgnore
    public static OperationDeclarations getOperationDeclarations(final ExecutorProperties properties) {
        OperationDeclarations declarations = null;

        final String declarationsPaths = properties.getProperty(OPERATION_DECLARATIONS);
        if (null != declarationsPaths) {
            declarations = OperationDeclarations.fromPaths(declarationsPaths);
        }

        if (null == declarations) {
            declarations = new OperationDeclarations.Builder().build();
        }

        return declarations;
    }

    public static Boolean getJobTrackerEnabled(final ExecutorProperties properties) {
        return Boolean.valueOf(properties.getProperty(JOB_TRACKER_ENABLED, "false"));
    }

    public static void setJobTrackerEnabled(final ExecutorProperties properties, final Boolean jobTrackerEnabled) {
        properties.setProperty(JOB_TRACKER_ENABLED, jobTrackerEnabled.toString());
    }

    public static String getOperationDeclarationPaths(final ExecutorProperties properties) {
        return properties.getProperty(OPERATION_DECLARATIONS);
    }

    public static void setOperationDeclarationPaths(final ExecutorProperties properties, final String paths) {
        properties.setProperty(OPERATION_DECLARATIONS, paths);
    }

    public static String getReflectionPackages(final ExecutorProperties properties) {
        return properties.getProperty(REFLECTION_PACKAGES);
    }

    public static void setReflectionPackages(final ExecutorProperties properties, final String packages) {
        properties.setProperty(REFLECTION_PACKAGES, packages);
        ReflectionUtil.addReflectionPackages(packages);
    }

    public static Integer getJobExecutorThreadCount(final ExecutorProperties properties) {
        return Integer.parseInt(properties.getProperty(EXECUTOR_SERVICE_THREAD_COUNT, EXECUTOR_SERVICE_THREAD_COUNT_DEFAULT));
    }

    public static void addOperationDeclarationPaths(final ExecutorProperties properties, final String... newPaths) {
        final String newPathsCsv = StringUtils.join(newPaths, ",");
        String combinedPaths = getOperationDeclarationPaths(properties);
        if (null == combinedPaths) {
            combinedPaths = newPathsCsv;
        } else {
            combinedPaths = combinedPaths + "," + newPathsCsv;
        }
        setOperationDeclarationPaths(properties, combinedPaths);
    }

    public static String getJsonSerialiserClass(final ExecutorProperties properties) {
        return properties.getProperty(JSON_SERIALISER_CLASS);
    }

    @JsonIgnore
    public static void setJsonSerialiserClass(final ExecutorProperties properties, final Class<? extends JSONSerialiser> jsonSerialiserClass) {
        setJsonSerialiserClass(properties, jsonSerialiserClass.getName());
    }

    public static void setJsonSerialiserClass(final ExecutorProperties properties, final String jsonSerialiserClass) {
        properties.setProperty(JSON_SERIALISER_CLASS, jsonSerialiserClass);
    }

    public static String getJsonSerialiserModules(final ExecutorProperties properties) {
        return properties.getProperty(JSON_SERIALISER_MODULES, "");
    }

    @JsonIgnore
    public static void setJsonSerialiserModules(final ExecutorProperties properties, final Set<Class<? extends JSONSerialiserModules>> modules) {
        final Set<String> moduleNames = new HashSet<>(modules.size());
        for (final Class module : modules) {
            moduleNames.add(module.getName());
        }
        setJsonSerialiserModules(properties, StringUtils.join(moduleNames, ","));
    }

    public static void setJsonSerialiserModules(final ExecutorProperties properties, final String modules) {
        properties.setProperty(JSON_SERIALISER_MODULES, modules);
    }

    public static Boolean getStrictJson(final ExecutorProperties properties) {
        final String strictJson = properties.getProperty(STRICT_JSON);
        return null == strictJson ? null : Boolean.parseBoolean(strictJson);
    }

    public static void setStrictJson(final ExecutorProperties properties, final Boolean strictJson) {
        properties.setProperty(STRICT_JSON, null == strictJson ? null : Boolean.toString(strictJson));
    }

    public static String getAdminAuth(final ExecutorProperties properties) {
        return properties.getProperty(ADMIN_AUTH, "");
    }

    public static void setAdminAuth(final ExecutorProperties properties, final String adminAuth) {
        properties.setProperty(ADMIN_AUTH, adminAuth);
    }

    public static void setCacheClass(final ExecutorProperties properties, final String cacheClass) {
        properties.setProperty(CACHE_CLASS, cacheClass);
    }

    public static void getCacheClass(final ExecutorProperties properties) {
        properties.getProperty(CACHE_CLASS);
    }
}
