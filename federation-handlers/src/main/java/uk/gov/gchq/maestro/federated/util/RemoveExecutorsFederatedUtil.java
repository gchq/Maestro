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
import uk.gov.gchq.maestro.federated.handler.RemoveExecutorHandler;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.user.User;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class RemoveExecutorsFederatedUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    private RemoveExecutorsFederatedUtil() {
        //No instance
    }

    public static boolean removeExecutorsFrom(final Executor executor,
                                              final Operation op, final User user) throws MaestroCheckedException {
        requireNonNull(op);
        return removeExecutorsFrom(executor, (String) op.get(RemoveExecutorHandler.EXECUTOR_ID), user);
    }

    public static boolean removeExecutorsFrom(final Executor executor,
                                              final String executorId, final User user) throws MaestroCheckedException {
        requireNonNull(executor);
        return removeExecutorsFrom(executor.getConfig(), executorId, user);
    }

    public static boolean removeExecutorsFrom(final Config config, final String executorId, final User user) throws MaestroCheckedException {
        requireNonNull(config);
        return removeExecutorsFrom(config.getProperties(), executorId, user);
    }

    public static boolean removeExecutorsFrom(final Map<String, Object> properties, final String executorId, final User user) throws MaestroCheckedException {
        final FederatedExecutorStorage executorStorage = ExecutorStorageFederatedUtil.getExecutorStorage(properties);
        requireNonNull(executorStorage);
        final boolean isRemoved = executorStorage.remove(executorId, user);
        return isRemoved;
    }
}
