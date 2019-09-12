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

package uk.gov.gchq.maestro.federated.handler;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.federated.util.GetAllExecutorsFederatedUtil;
import uk.gov.gchq.maestro.operation.Operation;

@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class GetAllExecutorIdsHandler implements OperationHandler {

    public static final String ERROR_GETTING_ALL_EXECUTOR_IDS_FROM_S_S = "Error getting AllExecutorIds from: %s -> %s";

    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        try {
            return GetAllExecutorsFederatedUtil.getAllExecutorsFrom(executor, context.getUser());
        } catch (final MaestroCheckedException e) {
            throw new OperationException(String.format(ERROR_GETTING_ALL_EXECUTOR_IDS_FROM_S_S, executor.getId(), e.getMessage()), e);
        }
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .toHashCode();
    }
}
