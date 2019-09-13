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

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.MaestroRuntimeException;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.operation.user.User;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static uk.gov.gchq.maestro.commonutil.exception.MaestroObjectsUtil.DUE_TO;
import static uk.gov.gchq.maestro.commonutil.exception.MaestroObjectsUtil.requireNonNull;

public final class GetExecutorsFederatedUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
    public static final String FROM_A_NULL = "Can't get Executors with a null %s";
    public static final String ERROR_GETTING_EXECUTORS = "Error getting executors";

    private GetExecutorsFederatedUtil() {
        //No instance
    }

    public static Collection<Executor> getExecutorsFrom(final Executor executor, final User user, final String executorId) throws MaestroCheckedException {
        requireNonNull(executorId, "executor ids", ERROR_GETTING_EXECUTORS);
        return getExecutorsFrom(executor, user, Lists.newArrayList(executorId));
    }

    public static Collection<Executor> getExecutorsFrom(final Executor executor, final User user, final List<String> executorIds) throws MaestroCheckedException {
        requireNonNull(executor, "Executor", getErrorPrefix(executorIds));
        return getExecutorsFrom(executor.getConfig(), user, executorIds);
    }

    private static Collection<Executor> getExecutorsFrom(final Config config, final User user, final List<String> executorIds) throws MaestroCheckedException {
        requireNonNull(config, "config", getErrorPrefix(executorIds));
        final FederatedExecutorStorage executorStorage;
        try {
            executorStorage = ExecutorStorageFederatedUtil.getExecutorStorage(config.getProperties());
        } catch (final Exception e) {
            final String message = getErrorPrefix(executorIds).get();
            LOGGER.error(message);
            throw new MaestroRuntimeException(message + DUE_TO + e.getMessage(), e);
        }
        return getExecutorsFrom(executorStorage, user, executorIds);
    }

    private static Collection<Executor> getExecutorsFrom(final FederatedExecutorStorage executorStorage, final User user, final List<String> executorIds) throws MaestroCheckedException {
        requireNonNull(executorStorage, "executorStorage", getErrorPrefix(executorIds));
        try {
            return executorStorage.get(user, executorIds);
        } catch (final Exception e) {
            LOGGER.error(ERROR_GETTING_EXECUTORS);
            throw new MaestroCheckedException(ERROR_GETTING_EXECUTORS + DUE_TO + e.getMessage(), e);
        }
    }

    private static Supplier<String> getErrorPrefix(final List<String> executorIds) {
        return () -> String.format("%s ids: %s", ERROR_GETTING_EXECUTORS, executorIds.toString());
    }
}
