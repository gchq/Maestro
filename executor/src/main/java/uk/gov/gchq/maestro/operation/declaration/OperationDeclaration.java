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

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import uk.gov.gchq.maestro.commonutil.ToStringBuilder;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;

/**
 * A single {@code OperationDeclaration} describes an operationId handler.
 */
public class OperationDeclaration {
    private String operationId;

    private OperationHandler handler;

    public String getOperationId() {
        return operationId;
    }

    public OperationDeclaration operationId(String operationId) {
        this.operationId = operationId;
        return this;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    public OperationHandler getHandler() {
        return handler;
    }

    public OperationDeclaration handler(final OperationHandler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("operationId", operationId)
                .append("handler", handler)
                .build();
    }

}
