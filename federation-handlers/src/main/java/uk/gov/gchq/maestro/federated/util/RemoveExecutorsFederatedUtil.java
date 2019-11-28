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

import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static uk.gov.gchq.maestro.commonutil.exception.MaestroObjectsUtil.DUE_TO;
import static uk.gov.gchq.maestro.commonutil.exception.MaestroObjectsUtil.requireNonNull;

public final class RemoveExecutorsFederatedUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveExecutorsFederatedUtil.class);
    public static final String ERROR_REMOVING_EXECUTORS = "Error removing executors";
    public static final String OPERATION_DID_HAVE_THE_REQUIRED_FIELD = "Operation did have the required field: %s found: %s";

    private RemoveExecutorsFederatedUtil() {
        //No instance
    }

    public static boolean removeExecutorsFrom(final Executor executor,
                                              final Operation op, final User user) throws MaestroCheckedException {

        requireNonNull(op, "operation", ERROR_REMOVING_EXECUTORS);
        final String executorId;
        final Object o = op.get(RemoveExecutorHandler.EXECUTOR_ID);
        try {
            executorId = (String) o;
        } catch (final ClassCastException e) {
            final String message = String.format(OPERATION_DID_HAVE_THE_REQUIRED_FIELD, RemoveExecutorHandler.EXECUTOR_ID, o);
            LOGGER.error(message);
            throw new MaestroCheckedException(message + DUE_TO + e.getMessage());
        }
        return removeExecutorsFrom(executor, executorId, user);
    }

    private static boolean removeExecutorsFrom(final Executor executor,
                                               final String executorId, final User user) throws MaestroCheckedException {
        requireNonNull(executor, "Executor", getErrorPrefix(executorId));
        return removeExecutorsFrom(executor.getConfig(), executorId, user);
    }

    private static boolean removeExecutorsFrom(final Config config, final String executorId, final User user) throws MaestroCheckedException {
        requireNonNull(config, "config", getErrorPrefix(executorId));
        final FederatedExecutorStorage executorStorage;
        try {
            executorStorage = ExecutorStorageFederatedUtil.getExecutorStorage(config.getProperties());
        } catch (final Exception e) {
            final String message = getErrorPrefix(executorId).get();
            LOGGER.error(message);
            throw new MaestroCheckedException(message + DUE_TO + e.getMessage(), e);
        }

        final boolean hasRemoved;

        if (isNull(executorStorage)) {
            LOGGER.warn("Trying to removing Executor:" + config.getId() + " from null executorStorage belonging to: " + executorId);
            hasRemoved = false;
        } else {
            requireNonNull(executorStorage, "executorStorage", getErrorPrefix(executorId));

            if (isNull(executorId) || executorId.isEmpty()) {
                LOGGER.warn("removing executorId that is: " + executorId);
            }

            hasRemoved = executorStorage.remove(executorId, user);
        }
        return hasRemoved;
    }

    public static Supplier<String> getErrorPrefix(final String executorId) {
        return () -> String.format("%sids: %s", ERROR_REMOVING_EXECUTORS, executorId);
    }
}
