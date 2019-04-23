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

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.federatedexecutor.FederatedAccess;
import uk.gov.gchq.maestro.federatedexecutor.FederatedExecutorStorage;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static java.util.Objects.nonNull;

public final class FederatedPropertiesUtil {
    public static final String EXECUTOR_STORAGE = "ExecutorStorage";
    public static final String VALUE_FOR_PROPERTY_KEY_S_EXPECTED_CLASS_S_FOUND_S = "value for property key: %s expected class: %s found: %s";
    public static final String ERROR_GETTING_DESERIALISED_FEDERATED_EXECUTOR_STORAGE_KEY_S_FROM_PROPERTIES_S = "Error getting deserialised FederatedExecutorStorage for key: %s from properties. -> %s";
    public static final String ERROR_MERGING_AND_ADDING_FEDERATED_EXECUTOR_STORAGE_TO_PROPERTIES_S = "Error merging and adding FederatedExecutorStorage to properties. -> %s";
    public static final String ERROR_SERIALISING_AND_ADDING_FEDERATED_EXECUTOR_STORAGE_TO_PROPERTIES_S = "Error serialising and adding FederatedExecutorStorage to properties. -> %s";

    private FederatedPropertiesUtil() {
        //empty
    }

    public static FederatedExecutorStorage getDeserialisedExecutorStorage(final Properties properties) throws MaestroCheckedException {
        try {
            FederatedExecutorStorage rtn = null;
            final Object o = properties.get(EXECUTOR_STORAGE);
            if (nonNull(o)) {
                if (o instanceof String) {
                    rtn = JSONSerialiser.deserialise((String) o, FederatedExecutorStorage.class);
                } else {
                    throw new MaestroCheckedException(String.format(VALUE_FOR_PROPERTY_KEY_S_EXPECTED_CLASS_S_FOUND_S, EXECUTOR_STORAGE, String.class.getCanonicalName(), o.getClass().getCanonicalName()));
                }
            }
            return rtn;
        } catch (final Exception e) {
            throw new MaestroCheckedException(String.format(ERROR_GETTING_DESERIALISED_FEDERATED_EXECUTOR_STORAGE_KEY_S_FROM_PROPERTIES_S, EXECUTOR_STORAGE, e.getMessage()), e);
        }
    }

    public static void addSerialisedExecutorStorage(final Properties properties, final FederatedExecutorStorage executorStorage) throws MaestroCheckedException {
        try {
            final FederatedExecutorStorage deserialisedExecutorStorage = getDeserialisedExecutorStorage(properties);
            if (nonNull(deserialisedExecutorStorage)) {
                for (final Map.Entry<FederatedAccess, Set<Executor>> entry : deserialisedExecutorStorage.getStorage().entrySet()) {
                    executorStorage.put(entry.getValue(), entry.getKey());
                }
            }

            putSerialisedExecutorStorage(properties, executorStorage);

        } catch (final Exception e) {
            throw new MaestroCheckedException(String.format(ERROR_MERGING_AND_ADDING_FEDERATED_EXECUTOR_STORAGE_TO_PROPERTIES_S, e.getMessage()), e);
        }
    }

    public static void putSerialisedExecutorStorage(final Properties properties, final FederatedExecutorStorage serialisedExecutorStorage) throws MaestroCheckedException {
        try {
            properties.put(EXECUTOR_STORAGE, new String(JSONSerialiser.serialise(serialisedExecutorStorage)));
        } catch (final SerialisationException e) {
            throw new MaestroCheckedException(String.format(ERROR_SERIALISING_AND_ADDING_FEDERATED_EXECUTOR_STORAGE_TO_PROPERTIES_S, e.getMessage()));
        }
    }
}
