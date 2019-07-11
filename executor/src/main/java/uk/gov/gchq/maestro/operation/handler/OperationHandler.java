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


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public interface OperationHandler {


    String OPERATION_DID_NOT_CONTAIN_REQUIRED_FIELDS = "Operation did not contain required fields. [";
    String FIELD_S_OF_TYPE_S = "Field:%s of Type:%s, ";

    default Object doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {

        final List<String> collect = getOperationErrorsForIncorrectValueType(operation);

        if (collect.isEmpty()) {
            return _doOperation(operation, context, executor);
        } else {
            final StringBuilder errorMessage = new StringBuilder().append(OPERATION_DID_NOT_CONTAIN_REQUIRED_FIELDS);
            collect.forEach(errorMessage::append);
            errorMessage.append(" ]");
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    default List<String> getOperationErrorsForIncorrectValueType(final Operation operation) {
        final FieldDeclaration fieldDeclaration = getFieldDeclaration();
        return fieldDeclaration.getFieldDeclarations().entrySet().stream()
                .filter(e -> {
                    final String key = e.getKey();
                    final boolean noKeyFoundInOperation = !operation.containsKey(key);
                    final boolean keyIsNotOptional = !fieldDeclaration.optionalContains(key); //case insensitive
                    final boolean noCompulsoryKeyFound = noKeyFoundInOperation && keyIsNotOptional;
                    final boolean iskeyInvalid;
                    if (noCompulsoryKeyFound) {
                        iskeyInvalid = true;
                    } else {
                        final Object value = operation.get(key);
                        //hasValueNotExpectedType
                        iskeyInvalid = nonNull(value) && !e.getValue().isInstance(value);
                    }
                    return iskeyInvalid;
                })
                .map(e -> String.format(FIELD_S_OF_TYPE_S, e.getKey(), e.getValue().getCanonicalName())).collect(Collectors.toList());
    }

    default List<String> getOperationErrorsForNullAndIncorrectValueType(final Operation operation) {
        final FieldDeclaration fieldDeclaration = this.getFieldDeclaration();
        return fieldDeclaration.getFieldDeclarations().entrySet().stream()
                .filter(e -> {
                    final String key = e.getKey();
                    final boolean noKeyFoundInOperation = !operation.containsKey(key);
                    final boolean keyIsNotOptional = !fieldDeclaration.optionalContains(key);//case insensitive
                    final boolean noCompulsoryKeyFound = noKeyFoundInOperation && keyIsNotOptional;
                    final boolean isKeyInvalid;
                    if (noCompulsoryKeyFound) {
                        isKeyInvalid = true;
                    } else {
                        final Object value = operation.get(key);
                        //noValueOrNotExpectedType
                        isKeyInvalid = isNull(value) || !e.getValue().isInstance(value);
                    }
                    return isKeyInvalid;
                })
                .map(e -> String.format(FIELD_S_OF_TYPE_S, e.getKey(), e.getValue().getCanonicalName())).collect(Collectors.toList());
    }

    Object _doOperation(Operation operation, Context context, Executor executor) throws OperationException;

    FieldDeclaration getFieldDeclaration();
}
