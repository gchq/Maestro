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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.commonutil.exception.MaestroObjectsUtil;
import uk.gov.gchq.maestro.commonutil.exception.MaestroRuntimeException;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.FederatedAccess;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;

import java.util.Map;
import java.util.TreeSet;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static uk.gov.gchq.maestro.commonutil.exception.MaestroObjectsUtil.requireNonNull;

public final class ExecutorStorageFederatedUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorStorageFederatedUtil.class);
    public static final String EXECUTOR_STORAGE = "executorStorage";
    public static final String ERROR_MERGING_AND_ADDING_FEDERATED_EXECUTOR_STORAGE_TO_PROPERTIES_S = "Error merging and adding FederatedExecutorStorage to properties. -> %s";
    public static final String ERROR_ADDING_EXECUTORS_TO_STORAGE = "Error adding Executors to FederatedExecutorStorage";
    public static final String ERROR_GETTING_FEDERATED_EXECUTOR_STORAGE = "Error getting FederatedExecutorStorage";
    public static final String ERROR_ADDING_FEDERATED_EXECUTOR_STORAGE = "Error adding FederatedExecutorStorage";
    public static final String DUE_TO = MaestroObjectsUtil.DUE_TO;

    private ExecutorStorageFederatedUtil() {
        //No instance
    }

    public static void addExecutorStorage(final Executor receivingExecutor, final FederatedExecutorStorage storage) {
        requireNonNull(receivingExecutor, "executor", ERROR_ADDING_EXECUTORS_TO_STORAGE);
        try {
            ExecutorStorageFederatedUtil.addExecutorStorage(receivingExecutor.getConfig(), storage);
        } catch (final Exception e) {
            LOGGER.error(ERROR_ADDING_FEDERATED_EXECUTOR_STORAGE);
            throw new RuntimeException(ERROR_ADDING_FEDERATED_EXECUTOR_STORAGE + DUE_TO + e.getMessage(), e);
        }
    }

    public static void addExecutorStorage(final Config receivingConfig, final FederatedExecutorStorage storageToAdd) {
        try {
            if (nonNull(storageToAdd)) {
                FederatedExecutorStorage receivingFEStorage = getExecutorStorage(receivingConfig);
                if (isNull(receivingFEStorage)) {
                    receivingFEStorage = new FederatedExecutorStorage();
                    setExecutorStorage(receivingConfig, receivingFEStorage);
                }
                final Map<FederatedAccess, TreeSet<Executor>> recievingStorage = receivingFEStorage.getStorage();
                for (final Map.Entry<FederatedAccess, TreeSet<Executor>> addingEntry : storageToAdd.getStorage().entrySet()) {
                    final FederatedAccess addingKey = addingEntry.getKey();
                    final TreeSet<Executor> addingValue = addingEntry.getValue();

                    if (recievingStorage.containsKey(addingKey)) {
                        recievingStorage.get(addingKey).addAll(addingValue);
                    } else {
                        recievingStorage.put(addingKey, addingValue);
                    }
                }
            }
        } catch (final Exception e) {
            final String message = String.format(ERROR_MERGING_AND_ADDING_FEDERATED_EXECUTOR_STORAGE_TO_PROPERTIES_S, e.getMessage());
            LOGGER.error(message);
            throw new MaestroRuntimeException(message + DUE_TO + e.getMessage(), e);
        }
    }

    public static void setExecutorStorage(final Executor executor, final FederatedExecutorStorage serialisedExecutorStorage) {
        requireNonNull(executor, "executor", ERROR_ADDING_FEDERATED_EXECUTOR_STORAGE);
        setExecutorStorage(executor.getConfig(), serialisedExecutorStorage);
    }


    public static void setExecutorStorage(final Config config, final FederatedExecutorStorage executorStorage) {
        requireNonNull(config, "Config", ERROR_ADDING_FEDERATED_EXECUTOR_STORAGE);
        setExecutorStorage(config.getProperties(), executorStorage);
    }

    public static void setExecutorStorage(final Map<String, Object> properties, final FederatedExecutorStorage executorStorage) {
        properties.put(EXECUTOR_STORAGE, executorStorage);
    }

    public static FederatedExecutorStorage getExecutorStorage(final Executor executor) {
        requireNonNull(executor, "Executor", ERROR_GETTING_FEDERATED_EXECUTOR_STORAGE);
        return getExecutorStorage(executor.getConfig());
    }

    public static FederatedExecutorStorage getExecutorStorage(final Config config) {
        requireNonNull(config, "config", ERROR_GETTING_FEDERATED_EXECUTOR_STORAGE);
        return getExecutorStorage(config.getProperties());
    }

    public static FederatedExecutorStorage getExecutorStorage(final Map<String, Object> properties) {
        requireNonNull(properties, "properties", ERROR_GETTING_FEDERATED_EXECUTOR_STORAGE);
        return (FederatedExecutorStorage) properties.get(EXECUTOR_STORAGE);
    }
}
