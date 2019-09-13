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
import uk.gov.gchq.maestro.commonutil.exception.MaestroObjectsUtil;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.federated.FederatedAccess;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.federated.handler.AddExecutorHandler;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.Set;

import static java.util.Objects.isNull;
import static uk.gov.gchq.maestro.commonutil.exception.MaestroObjectsUtil.requireNonNull;


public final class AddExecutorsFederatedUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddExecutorsFederatedUtil.class);
    public static final String ERROR_ADDING_EXECUTOR = "Error adding executor";
    public static final String DUE_TO = MaestroObjectsUtil.DUE_TO;

    private AddExecutorsFederatedUtil() {
        //No instance
    }

    public static Executor addExecutorTo(final Executor receivingExecutor,
                                         final Operation addExecutorOperation,
                                         final Context context) throws MaestroCheckedException {
        requireNonNull(addExecutorOperation, "operation", ERROR_ADDING_EXECUTOR);
        requireNonNull(context, "context", ERROR_ADDING_EXECUTOR);
        final Set<String> auths = (Set<String>) addExecutorOperation.get(AddExecutorHandler.AUTHS);
        final boolean isPublic = (boolean) addExecutorOperation.getOrDefault(AddExecutorHandler.IS_PUBLIC, false);
        final boolean disabledByDefault = (boolean) addExecutorOperation.getOrDefault(AddExecutorHandler.DISABLED_BY_DEFAULT, false);
        final Executor subExecutor = (Executor) addExecutorOperation.get(AddExecutorHandler.EXECUTOR);

        return addExecutorTo(receivingExecutor, context.getUser().getUserId(), auths, isPublic, disabledByDefault, subExecutor);
    }

    public static Executor addExecutorTo(final Executor receivingExecutor,
                                         final String userId,
                                         final Set<String> auths,
                                         final boolean isPublic,
                                         final boolean disabledByDefault,
                                         final Executor subExecutor) throws MaestroCheckedException {
        requireNonNull(receivingExecutor, "executor", ERROR_ADDING_EXECUTOR);
        requireNonNull(userId, "userId", ERROR_ADDING_EXECUTOR);
        if (isNull(subExecutor)) {
            LOGGER.warn("Executor to be added is null");
        }

        final FederatedAccess federatedAccess = new FederatedAccess(auths, userId, isPublic, disabledByDefault);

        final FederatedExecutorStorage storage = getStorage(subExecutor, federatedAccess);

        ExecutorStorageFederatedUtil.addExecutorStorage(receivingExecutor, storage);

        return receivingExecutor;
    }


    private static FederatedExecutorStorage getStorage(final Executor subExecutor, final FederatedAccess federatedAccess) throws MaestroCheckedException {
        final FederatedExecutorStorage storage;
        try {
            storage = new FederatedExecutorStorage()
                    .put(subExecutor, federatedAccess);
        } catch (final Exception e) {
            final String newMessage = ERROR_ADDING_EXECUTOR + DUE_TO + "Error adding executor to federatedExecutorStorage";
            LOGGER.error(newMessage);
            throw new MaestroCheckedException(newMessage + DUE_TO + e.getMessage(), e);
        }
        return storage;
    }


}
