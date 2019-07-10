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

package uk.gov.gchq.maestro.helper;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.ToStringBuilder;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;

@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class TestHandler implements OperationHandler {

    public static final String FIELD = "field";
    private String handlerField;

    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) {
        return handlerField + "," + operation.get(FIELD);
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration()
                .field(FIELD, String.class);
    }

    public String getHandlerField() {
        return handlerField;
    }

    public TestHandler handlerField(final String handlerField) {
        this.handlerField = handlerField;
        return this;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TestHandler testHandler = (TestHandler) obj;

        return new EqualsBuilder().append(handlerField, testHandler.handlerField).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(handlerField).build();
    }
}
