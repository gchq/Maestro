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
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.federated.FederatedAccess;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.federated.handler.AddExecutorHandler;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.Set;

public final class AddExecutorsFederatedUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    private AddExecutorsFederatedUtil() {
        //No instance
    }

    public static void addExecutorTo(final Executor executor, final Operation operation, final Context context) throws MaestroCheckedException {
        final Set<String> graphAuths = (Set<String>) operation.get(AddExecutorHandler.AUTHS);
        final Boolean isPublic = (Boolean) operation.getOrDefault(AddExecutorHandler.IS_PUBLIC, false);
        final Boolean disabledByDefault = (Boolean) operation.getOrDefault(AddExecutorHandler.DISABLED_BY_DEFAULT, false);
        final Executor subExecutor = (Executor) operation.get(AddExecutorHandler.EXECUTOR);

        addExecutorTo(executor, context.getUser().getUserId(), graphAuths, isPublic, disabledByDefault, subExecutor);
    }

    public static Executor addExecutorTo(final Executor parent,
                                         final String userId, final Set<String> graphAuths,
                                         final Boolean isPublic, final Boolean disabledByDefault,
                                         final Executor subExecutor) throws MaestroCheckedException {
        final FederatedAccess federatedAccess = new FederatedAccess(graphAuths, userId, isPublic, disabledByDefault);

        final FederatedExecutorStorage storage = new FederatedExecutorStorage()
                .put(subExecutor, federatedAccess);

        ExecutorStorageFederatedUtil.addExecutorStorage(parent.getConfig(), storage);

        return parent;
    }


}
