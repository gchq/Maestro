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
import uk.gov.gchq.maestro.federated.util.RemoveExecutorsFederatedUtil;
import uk.gov.gchq.maestro.operation.Operation;

@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class RemoveExecutorHandler implements OperationHandler {

    public static final String ERROR_REMOVING_EXECUTOR_ID_S_FROM_S_S = "Error removing executorId: %s from: %s -> %s";
    public static final String EXECUTOR_ID = "executorId";

    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        final boolean isRemoved;
        try {
            isRemoved = RemoveExecutorsFederatedUtil.removeExecutorsFrom(executor, operation, context.getUser());
        } catch (final MaestroCheckedException e) {
            throw new OperationException(String.format(ERROR_REMOVING_EXECUTOR_ID_S_FROM_S_S, operation.get(EXECUTOR_ID), executor.getId(), e.getMessage()), e);
        }

        return isRemoved;
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration()
                .field(EXECUTOR_ID, String.class);
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
