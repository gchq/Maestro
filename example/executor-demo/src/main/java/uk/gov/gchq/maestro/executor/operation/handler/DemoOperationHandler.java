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

package uk.gov.gchq.maestro.executor.operation.handler;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;
import uk.gov.gchq.maestro.rest.service.v2.OperationServiceV2;

@JsonPropertyOrder(value = {"class", "operationName", "value"}, alphabetic = true)
public class DemoOperationHandler implements OperationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoOperationHandler.class);
    public static final String S_WAS_SUPPORTED_BY_EXECUTOR_S_D_MULTIPLIED_BY_D_IS_D = "%s was supported by Executor: %s. %d multiplied by %d is %d";
    public static final String VALUE = "value";
    private Integer multiply;

    @JsonCreator
    public DemoOperationHandler(@JsonProperty("multiply") final Integer multiply) {
        this.multiply = multiply;
    }

    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        final Integer input = (Integer) operation.get(VALUE);
        final Integer output = multiply * input;
        final String format = String.format(S_WAS_SUPPORTED_BY_EXECUTOR_S_D_MULTIPLIED_BY_D_IS_D, operation.getId(), executor.getId(), input, multiply, output);
        LOGGER.info(format);
        return format;
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration()
                .fieldRequired(VALUE, Integer.class)
                .fieldOptional(OperationServiceV2.OUTPUT_TYPE_REFERENCE, TypeReferenceImpl.Map.class);
    }

    public int getMultiply() {
        return multiply;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            final boolean b = false;
            return b;
        }

        final DemoOperationHandler that = (DemoOperationHandler) o;

        return new EqualsBuilder()
                .append(multiply, that.multiply)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(multiply)
                .toHashCode();
    }
}
