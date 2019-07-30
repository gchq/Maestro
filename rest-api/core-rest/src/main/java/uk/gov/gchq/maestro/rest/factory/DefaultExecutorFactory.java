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
package uk.gov.gchq.maestro.rest.factory;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroRuntimeException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.rest.SystemProperty;
import uk.gov.gchq.maestro.util.Config;
import uk.gov.gchq.maestro.util.ExecutorPropertiesUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Default implementation of the {@link ExecutorFactory} interface, used by HK2 to
 * instantiate default {@link uk.gov.gchq.maestro.Executor} instances.
 */
public class DefaultExecutorFactory implements ExecutorFactory {
    private static Executor executor;

    /**
     * Set to true by default - so the same instance of {@link Executor} will be
     * returned.
     */
    private boolean singletonExecutor = true;

    public DefaultExecutorFactory() {
        // Executor factories should be constructed via the createExecutorFactory static method,
        // public constructor is required only by HK2
    }

    public static ExecutorFactory createExecutorFactory() {
        final String executorFactoryClass = System.getProperty(SystemProperty.EXECUTOR_FACTORY_CLASS,
                SystemProperty.EXECUTOR_FACTORY_CLASS_DEFAULT);

        try {
            return Class.forName(executorFactoryClass)
                    .asSubclass(ExecutorFactory.class)
                    .newInstance();
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to create executor factory from class: " + executorFactoryClass, e);
        }
    }

    protected static Path[] getSchemaPaths() {
        final String schemaPaths = System.getProperty(SystemProperty.SCHEMA_PATHS);
        if (null == schemaPaths) {
            return new Path[0];
        }

        final String[] schemaPathsArray = schemaPaths.split(",");
        final Path[] paths = new Path[schemaPathsArray.length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = Paths.get(schemaPathsArray[i]);
        }

        return paths;
    }

    @Override
    public Executor getExecutor() {
        if (singletonExecutor) {
            if (null == executor) {
                setExecutor(createExecutor());
            }
            return executor;
        }

        return createExecutor();
    }

    public static void setExecutor(final Executor executor) {
        DefaultExecutorFactory.executor = executor;
    }

    public boolean isSingletonExecutor() {
        return singletonExecutor;
    }

    public void setSingletonExecutor(final boolean singletonExecutor) {
        this.singletonExecutor = singletonExecutor;
    }

    @Override
    public Executor createExecutor() {
        final Executor executor = new Executor();

        resolveConfig(executor);

        return executor;
    }

    public void resolveConfig(final Executor executor) {
        final String executorConfigPath = System.getProperty(SystemProperty.EXECUTOR_CONFIG_PATH);
        if (null != executorConfigPath) {
            final Config deserialisedConfig;
            try {
                final Path path = Paths.get(executorConfigPath);
                final byte[] bytes;
                try {
                    bytes = (null != path) ? Files.readAllBytes(path) : null;
                } catch (final IOException e) {
                    throw new IllegalArgumentException("Unable to read config from path: " + path, e);
                }
                deserialisedConfig = JSONSerialiser.deserialise(bytes, Config.class);
            } catch (final SerialisationException e) {
                throw new IllegalArgumentException("Unable to deserialise config", e);
            }
            executor.config(deserialisedConfig);
        }
    }

    public Properties getProperties() {
        final String storePropertiesPath = System.getProperty(SystemProperty.MAESTRO_PROPERTIES_PATH);
        if (null == storePropertiesPath) {
            throw new MaestroRuntimeException("The path to the Store Properties was not found in system properties for key: " + SystemProperty.MAESTRO_PROPERTIES_PATH);
        }
        return ExecutorPropertiesUtil.loadProperties(storePropertiesPath);
    }
}
