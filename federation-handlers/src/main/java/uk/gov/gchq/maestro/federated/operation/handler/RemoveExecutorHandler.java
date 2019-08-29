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

package uk.gov.gchq.maestro.federated.operation.handler;

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.federated.operation.RemoveExecutor;
import uk.gov.gchq.maestro.federated.util.FederatedHandlersUtil;

public class RemoveExecutorHandler implements OperationHandler<RemoveExecutor> {

    public static final String ERROR_REMOVING_EXECUTOR_ID_S_FROM_S_S = "Error removing executorId: %s from: %s -> %s";

    @Override
    public Object doOperation(final RemoveExecutor operation, final Context context, final Executor executor) throws OperationException {

        final boolean isRemoved;
        try {
            isRemoved = FederatedHandlersUtil.removeExecutorsFrom(executor, operation, context.getUser());
        } catch (final MaestroCheckedException e) {
            throw new OperationException(String.format(ERROR_REMOVING_EXECUTOR_ID_S_FROM_S_S, operation.getGraphId(), executor.getConfig().getId(), e.getMessage()), e);
        }

        return null;
    }
}
