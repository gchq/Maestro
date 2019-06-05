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
package uk.gov.gchq.maestro.operation.handler;


import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface OperationHandler {


    default Object doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {

        final List<String> collect = getOperationErrorsForIncorrectValueType(operation);

        if (collect.isEmpty()) {
            return _doOperation(operation, context, executor);
        } else {
            final StringBuilder errorMessage = new StringBuilder().append("Operation did not contain required fields");
            collect.forEach(errorMessage::append);
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    default List<String> getOperationErrorsForIncorrectValueType(final Operation operation) {
        return getFieldDeclaration().getFieldDeclarations().entrySet().stream()
                .filter(e -> {
                    final Object opValue = operation.get(e.getKey());
                    return (Objects.isNull(opValue) || e.getValue().isInstance(operation.get(e.getKey())));
                })
                .map(e -> String.format("Field:%s of Type:%s", e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    default List<String> getOperationErrorsForNullAndIncorrectValueType(final Operation operation) {
        return getFieldDeclaration().getFieldDeclarations().entrySet().stream()
                .filter(e -> {
                    final Object opValue = operation.get(e.getKey());
                    return (Objects.nonNull(opValue) && e.getValue().isInstance(operation.get(e.getKey())));
                })
                .map(e -> String.format("Field:%s of Type:%s", e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    Object _doOperation(Operation operation, Context context, Executor executor) throws OperationException;

    FieldDeclaration getFieldDeclaration();
}
