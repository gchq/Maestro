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

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.operation.user.User;

import java.util.Collection;

import static uk.gov.gchq.maestro.commonutil.exception.MaestroObjectsUtil.DUE_TO;
import static uk.gov.gchq.maestro.commonutil.exception.MaestroObjectsUtil.requireNonNull;

public final class GetAllExecutorsFederatedUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
    public static final String ERROR_GETTING_ALL_EXECUTORS = "Error getting all executors";

    private GetAllExecutorsFederatedUtil() {
        //No instance
    }

    public static Collection<Executor> getAllExecutorsFrom(final Executor executor, final User user) throws MaestroCheckedException {
        requireNonNull(executor, "Executor", ERROR_GETTING_ALL_EXECUTORS);
        final Config config = executor.getConfig();
        return getAllExecutorsFrom(config, user);
    }

    private static Collection<Executor> getAllExecutorsFrom(final Config config, final User user) throws MaestroCheckedException {
        requireNonNull(config, "Config", ERROR_GETTING_ALL_EXECUTORS);
        final FederatedExecutorStorage executorStorage;
        try {
            executorStorage = ExecutorStorageFederatedUtil.getExecutorStorage(config.getProperties());
        } catch (final Exception e) {
            LOGGER.error(ERROR_GETTING_ALL_EXECUTORS);
            throw new MaestroCheckedException(ERROR_GETTING_ALL_EXECUTORS + DUE_TO + e.getMessage(), e);
        }
        return getAllExecutorsFrom(user, executorStorage);
    }

    private static Collection<Executor> getAllExecutorsFrom(final User user, final FederatedExecutorStorage executorStorage) {
        requireNonNull(executorStorage, "FederatedExecutorStorage", ERROR_GETTING_ALL_EXECUTORS);
        return executorStorage.getAll(user);
    }
}
