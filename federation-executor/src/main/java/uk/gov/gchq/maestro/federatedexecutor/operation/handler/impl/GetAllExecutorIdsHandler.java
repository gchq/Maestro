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

package uk.gov.gchq.maestro.federatedexecutor.operation.handler.impl;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.federatedexecutor.operation.GetAllExecutorIds;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.util.FederatedHandlersUtil;

public class GetAllExecutorIdsHandler implements OperationHandler<GetAllExecutorIds> {

    public static final String ERROR_GETTING_ALL_EXECUTOR_IDS_FROM_S_S = "Error getting AllExecutorIds from: %s -> %s";

    @Override
    public Object doOperation(final GetAllExecutorIds operation, final Context context, final Executor executor) throws OperationException {
        try {
           return FederatedHandlersUtil.getAllExecutorsFrom(executor, context.getUser());
        } catch (MaestroCheckedException e) {
            throw new OperationException(String.format(ERROR_GETTING_ALL_EXECUTOR_IDS_FROM_S_S, executor.getConfig().getId(), e.getMessage()), e);
        }
    }
}
