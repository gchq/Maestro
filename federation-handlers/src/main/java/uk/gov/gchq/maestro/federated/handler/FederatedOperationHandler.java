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
import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.koryphe.binaryoperator.KorypheBinaryOperator;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.federated.util.GetExecutorsFederatedUtil;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class FederatedOperationHandler implements OperationHandler {

    public static final String IDS = "ids";
    public static final String OPERATION = "operation";
    public static final String MERGE_OPERATION = "mergeOperation";

    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        final List<Executor> executorsFrom = new ArrayList<>();
        final List<String> ids = (List<String>) operation.get(IDS);
        final KorypheBinaryOperator mergeOperation = (KorypheBinaryOperator) operation.get(MERGE_OPERATION);

        try {
            final Collection<Executor> executorsFrom1 = GetExecutorsFederatedUtil.
                    getExecutorsFrom(executor, context.getUser(), Lists.newArrayList(ids));
            executorsFrom.addAll(executorsFrom1);
        } catch (final MaestroCheckedException e) {
            throw new OperationException(String.format("Error executing FederatedOperation -> %s", e.getMessage()), e);
        }

        Object state = null;
        for (final Executor sub : executorsFrom) {
            final Object execute = sub.execute((Operation) operation.get(OPERATION), context);
            state = mergeOperation.apply(state, execute);
        }

        return state;
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration()
                .fieldRequired(MERGE_OPERATION, KorypheBinaryOperator.class)
                .fieldRequired(OPERATION, Operation.class)
                .fieldRequired(IDS, List.class); //whitelist
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
