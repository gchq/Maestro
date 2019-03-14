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
package uk.gov.gchq.maestro;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;


@JsonPropertyOrder(value = {"class", "operationHandlerMap", "config"}, alphabetic = true)
public class Executor {
    public static final String OPERATION_S_IS_NOT_SUPPORTED_BY_THE_S = "Operation %s is not supported by the %s.";
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
    private final Map<Class<? extends Operation>, OperationHandler> operationHandlerMap = new HashMap<>();
    private final Map<String, String> config = new HashMap<>();

    public Executor() {
    }

    @JsonCreator
    public Executor(@JsonProperty("operationHandlerMap") Map<Class<? extends Operation>, OperationHandler> operationHandlerMap,
                    @JsonProperty("config") Map<String, String> config) {
        if (nonNull(operationHandlerMap) && !operationHandlerMap.isEmpty()) {
            this.operationHandlerMap.putAll(operationHandlerMap);
        }

        if (nonNull(config) && !config.isEmpty()) {
            this.config.putAll(config);
        }
    }


    public <O> O execute(final Operation operation, final Context context) {
        return (O) handleOperation(operation, context);
    }

    private Object handleOperation(final Operation operation, final Context context) {
        Object result;
        OperationHandler<Operation> handler = getHandler(operation.getClass());

        if (null != handler) {
            result = handler.doOperation(operation, context, this);
        } else if (operation instanceof DefaultOperation) {
            result = doUnhandledOperation(operation, context);
        } else {
            result = this.handleOperation(new DefaultOperation().setWrappedOp(operation), context);
        }

        return result;
    }


    private Object doUnhandledOperation(final Operation operation, final Context context) {
        throw new UnsupportedOperationException(String.format(OPERATION_S_IS_NOT_SUPPORTED_BY_THE_S, operation.getClass(), this.getClass().getSimpleName()));
    }

    @JsonIgnore
    private OperationHandler<Operation> getHandler(final Class<? extends Operation> operation) {
        return operationHandlerMap.get(operation);
    }

    public Map<Class<? extends Operation>, OperationHandler> getOperationHandlerMap() {
        return ImmutableMap.copyOf(operationHandlerMap);
    }

    public Executor operationHandlerMap(Map<Class<? extends Operation>, OperationHandler> operationHandlerMap) {
        this.operationHandlerMap.clear();
        this.operationHandlerMap.putAll(operationHandlerMap);
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final Executor executor = (Executor) o;

        return new EqualsBuilder()
                .append(operationHandlerMap, executor.operationHandlerMap)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(operationHandlerMap)
                .toHashCode();
    }

    @JsonGetter("class") //TODO improvement
    public String getClassName() {
        return this.getClass().getCanonicalName();
    }

    @JsonGetter("config")
    public Map<String, String> getConfig() {
        return ImmutableMap.copyOf(config);
    }

    public Executor config(Map<String, String> config) {
        this.config.clear();
        this.config.putAll(config);
        return this;
    }

}
