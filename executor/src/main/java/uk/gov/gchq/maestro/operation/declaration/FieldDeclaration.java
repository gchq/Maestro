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
package uk.gov.gchq.maestro.operation.declaration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import uk.gov.gchq.maestro.commonutil.ToStringBuilder;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * A single {@code OperationDeclaration} describes an operationId handlerClass.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
@JsonPropertyOrder(value = {"class", "handlerClass"}, alphabetic = true)
public class FieldDeclaration {
    private final Class<? extends OperationHandler> handlerClass;
    private Map<String, Class> fieldDeclarations = new HashMap<>();

    @JsonCreator
    public FieldDeclaration(@JsonProperty("handlerClass") final Class<? extends OperationHandler> handlerClass, @JsonProperty("fieldDeclarations") final Map<String, Class> fieldDeclarations) {
        this.handlerClass = handlerClass;
        this.fieldDeclarations = fieldDeclarations;
    }

    public FieldDeclaration(final Class<? extends OperationHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    public Class<? extends OperationHandler> getHandlerClass() {
        return handlerClass;
    }

    public Map<String, Class> getFieldDeclarations() {
        return fieldDeclarations;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("handlerClass", handlerClass)
                .build();
    }

    public FieldDeclaration field(final String field, final Class valueClass) {
        fieldDeclarations.put(field, valueClass);
        return this;
    }
}
