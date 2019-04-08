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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.ExecutorProperties;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.federatedexecutor.operation.AddExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public final class FederatedUtil {
    public static final String EXECUTOR_STORE = "ExecutorStore_";
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
    public static final String ERROR_DESERIALISING_EXECUTOR_FROM_PROPERTY_VALUE_STRING = "Error deserialising Executor from property value string. Key: \"%s\" Value: \"%s\"";
    public static final String VALUE_FOR_PROPERTY_S_EXPECTED_STRING_FOUND_S = "value for property: %s class expected: String found: %s";

    private FederatedUtil() {
        //No instance
    }

    public static HashMap<String, Executor> getFederatedExecutors(final Executor executor) throws MaestroCheckedException {
        final Config config = executor.getConfig();
        return getFederatedExecutors(config);
    }

    public static HashMap<String, Executor> getFederatedExecutors(final Config config) throws MaestroCheckedException {
        final ExecutorProperties properties = config.getProperties();
        return getFederatedExecutors(properties);
    }

    public static HashMap<String, Executor> getFederatedExecutors(final ExecutorProperties properties) throws MaestroCheckedException {
        return getFederatedExecutors(properties.getProperties());
    }

    public static HashMap<String, Executor> getFederatedExecutors(final Properties properties) throws MaestroCheckedException {
        HashMap<String, Executor> rtn = new HashMap<>();
        for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
            final Object key = entry.getKey();
            if (nonNull(key) && key instanceof String) {
                final String keyString = (String) key;
                if (keyString.startsWith(EXECUTOR_STORE)) {
                    final Object value = entry.getValue();
                    //Not possible HashMap backed Properties should not accept null as value, no need to null check.
                    if (!(value instanceof String)) {
                        final String format = String.format(VALUE_FOR_PROPERTY_S_EXPECTED_STRING_FOUND_S, keyString, value.getClass());
                        LOGGER.error(format);
                        throw new MaestroCheckedException(format);
                    } else {
                        try {
                            final Executor executor = JSONSerialiser.deserialise((String) value, Executor.class);
                            rtn.put(keyString.substring(EXECUTOR_STORE.length()), executor);
                        } catch (final SerialisationException e) {
                            final String format = String.format(ERROR_DESERIALISING_EXECUTOR_FROM_PROPERTY_VALUE_STRING, keyString, value);
                            LOGGER.error(format);
                            throw new MaestroCheckedException(format, e);
                        }
                    }
                }
            }
        }
        return rtn;
    }

    public static Executor addExecutorTo(final Executor parent, final AddExecutor op) {
        addExecutorTo(parent.getConfig(), op);
        return parent;
    }

    public static Config addExecutorTo(final Config config, final AddExecutor op) {
        addExecutorTo(config.getProperties(), op);
        return config;
    }

    public static ExecutorProperties addExecutorTo(final ExecutorProperties properties, final AddExecutor op) {
        addExecutorTo(properties.getProperties(), op);
        return properties;
    }

    public static Properties addExecutorTo(final Properties properties, final AddExecutor op) {
        // TODO op.getAuths();
        // TODO op.isPublic();

        final Config config = op.getConfig();
        requireNonNull(config, "Config from AddExecutor op is null.");
        final String id = config.getId();
        requireNonNull(id, "Id from Config is null");
        properties.put(FederatedUtil.EXECUTOR_STORE + id, new Executor(config));
        return properties;
    }
}
