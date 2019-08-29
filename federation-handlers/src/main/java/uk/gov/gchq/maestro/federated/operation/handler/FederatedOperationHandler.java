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

import com.google.common.collect.Lists;

import uk.gov.gchq.koryphe.binaryoperator.KorypheBinaryOperator;
import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.federated.operation.FederatedOperation;
import uk.gov.gchq.maestro.federated.util.FederatedHandlersUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FederatedOperationHandler implements OperationHandler<FederatedOperation> {
    @Override
    public Object doOperation(final FederatedOperation operation, final Context context, final Executor executor) throws OperationException {
        final Set<String> ids = operation.getIds();
        final List<Executor> executorsFrom = new ArrayList<>();
        final KorypheBinaryOperator mergeOperation = operation.getMergeOperation();
        Objects.requireNonNull(mergeOperation, "mergeOperation");

        try {
            executorsFrom.addAll(FederatedHandlersUtil.getExecutorsFrom(executor, context.getUser(), Lists.newArrayList(ids)));
        } catch (final MaestroCheckedException e) {
            throw new OperationException(String.format("Error executing FederatedOperation -> %s", e.getMessage()), e);
        }

        Object state = null;
        for (final Executor sub : executorsFrom) {
            final Object execute = sub.execute(operation.getOperation(), context);
            state = mergeOperation.apply(state, execute);
        }

        return state;
    }
}
