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

package uk.gov.gchq.maestro.federated.util;

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.FederatedAccess;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;

import java.util.Map;
import java.util.TreeSet;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public final class ExecutorStorageFederatedUtil {
    public static final String EXECUTOR_STORAGE = "executorStorage";
    public static final String ERROR_MERGING_AND_ADDING_FEDERATED_EXECUTOR_STORAGE_TO_PROPERTIES_S = "Error merging and adding FederatedExecutorStorage to properties. -> %s";

    private ExecutorStorageFederatedUtil() {
        //empty
    }

    public static void addExecutorStorage(final Executor executor, final FederatedExecutorStorage executorStorage) throws MaestroCheckedException {
        requireNonNull(executor, "Can't add ExecutorStorage to Null executor");
        //TODO wrap null pointer Exception
        addExecutorStorage(executor.getConfig(), executorStorage);
    }

    public static void addExecutorStorage(final Config receivingConfig, final FederatedExecutorStorage storageToAdd) throws MaestroCheckedException {
        try {
            if (nonNull(storageToAdd)) {
                FederatedExecutorStorage receivingStorage = ExecutorStorageFederatedUtil.getExecutorStorage(receivingConfig);
                if (isNull(receivingStorage)) {
                    receivingStorage = new FederatedExecutorStorage();
                    ExecutorStorageFederatedUtil.setExecutorStorage(receivingConfig, receivingStorage);
                }
                final Map<FederatedAccess, TreeSet<Executor>> recievingMap = receivingStorage.getStorage();
                for (final Map.Entry<FederatedAccess, TreeSet<Executor>> eToAdd : storageToAdd.getStorage().entrySet()) {
                    final FederatedAccess addingKey = eToAdd.getKey();
                    final TreeSet<Executor> addingValue = eToAdd.getValue();

                    if (recievingMap.containsKey(addingKey)) {
                        recievingMap.get(addingKey).addAll(addingValue);
                    } else {
                        recievingMap.put(addingKey, addingValue);
                    }
                }
            }
        } catch (final Exception e) {
            throw new MaestroCheckedException(String.format(ERROR_MERGING_AND_ADDING_FEDERATED_EXECUTOR_STORAGE_TO_PROPERTIES_S, e.getMessage()), e);
        }
    }

    public static void setExecutorStorage(final Executor executor, final FederatedExecutorStorage serialisedExecutorStorage) throws MaestroCheckedException {
        setExecutorStorage(executor.getConfig(), serialisedExecutorStorage);
    }


    public static void setExecutorStorage(final Config config, final FederatedExecutorStorage executorStorage) throws MaestroCheckedException {
        setExecutorStorage(config.getProperties(), executorStorage);
    }

    public static void setExecutorStorage(final Map<String, Object> properties, final FederatedExecutorStorage executorStorage) throws MaestroCheckedException {
        properties.put(EXECUTOR_STORAGE, executorStorage);
    }

    public static FederatedExecutorStorage getExecutorStorage(final Executor executor) throws MaestroCheckedException {
        requireNonNull(executor);
        return getExecutorStorage(executor.getConfig());
    }

    public static FederatedExecutorStorage getExecutorStorage(final Config config) throws MaestroCheckedException {
        requireNonNull(config);
        return getExecutorStorage(config.getProperties());
    }

    public static FederatedExecutorStorage getExecutorStorage(final Map<String, Object> properties) throws MaestroCheckedException {
        return (FederatedExecutorStorage) properties.get(EXECUTOR_STORAGE);
    }
}
