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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.exception.SerialisationException;
import uk.gov.gchq.maestro.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.DefaultOperation;
import uk.gov.gchq.maestro.operation.DoGetOperation;
import uk.gov.gchq.maestro.util.Config;

import java.util.Map;

import static java.util.Objects.nonNull;


@JsonPropertyOrder(value = {"class", "config"}, alphabetic = true)
public class Executor {
    public static final String OPERATION_S_IS_NOT_SUPPORTED_BY_THE_S = "DoGetOperation %s is not supported by the %s.";
    public static final String ERROR_DESERIALISE_EXECUTOR = "Could not deserialise Executor from given byte[]";
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
    private Config config;
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    /* TODO This is just as a note to remind that this must be removed and added
    // as a util
    //private JobTracker jobTracker;
    */

    public Executor() {
        this.config = new Config();
    }

    public Executor(final Config config) {
        this.config = config;
    }

    public Executor(final Map<Class<? extends DoGetOperation>, OperationHandler> operationHandlerMap,
                    final Map<String, String> config) {
        this();
        if (nonNull(operationHandlerMap) && !operationHandlerMap.isEmpty()) {
            this.config.getOperationHandlers().putAll(operationHandlerMap);
        }
    }

    public static Executor deserialise(final byte[] jsonBytes) {
        try {
            LOGGER.info("Deserialising Executor from byte[]");
            return JSONSerialiser.deserialise(jsonBytes, Executor.class);
        } catch (final SerialisationException e) {
            LOGGER.error(ERROR_DESERIALISE_EXECUTOR);
            throw new IllegalArgumentException(ERROR_DESERIALISE_EXECUTOR, e);
        }
    }

    public static Executor deserialise(final String josnString) {
        try {
            LOGGER.info("Deserialising Executor from byte[]");
            return JSONSerialiser.deserialise(josnString, Executor.class);
        } catch (final SerialisationException e) {
            LOGGER.error(ERROR_DESERIALISE_EXECUTOR);
            throw new IllegalArgumentException(ERROR_DESERIALISE_EXECUTOR, e);
        }
    }


    public <O, Output extends DoGetOperation<O>> O execute(final Output operation, final Context context) {
        return handleOperation(operation, context);
    }

    private <O> O handleOperation(final DoGetOperation<O> operation, final Context context) {
        O result;
        OperationHandler<O, DoGetOperation<O>> handler = getHandler(operation);

        if (null != handler) {
            result = handler.doOperation(operation, context, this);
        } else if (operation instanceof DefaultOperation) {
            result = doUnhandledOperation(operation);
        } else {
            final DoGetOperation<O> defaultOp = new DefaultOperation().setWrappedOp(operation);
            result = this.handleOperation(defaultOp, context);
        }

        return result;
    }

    private <O, Op extends DoGetOperation<O>> O doUnhandledOperation(final Op operation) {
        throw new UnsupportedOperationException(String.format(OPERATION_S_IS_NOT_SUPPORTED_BY_THE_S, operation.getClass(), this.getClass().getSimpleName()));
    }

    @JsonIgnore
    private <O, Op extends DoGetOperation<O>> OperationHandler<O, Op> getHandler(final Op operation) {
        return config.getOperationHandler(operation.getClass());
    }

    @JsonIgnore
    public Map<Class<? extends DoGetOperation>, OperationHandler> getOperationHandlerMap() {
        return ImmutableMap.copyOf(config.getOperationHandlers());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Executor executor = (Executor) o;

        return new EqualsBuilder()
                .append(config, executor.config)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(config)
                .toHashCode();
    }

    @JsonGetter("class") //TODO improvement
    public String getClassName() {
        return this.getClass().getCanonicalName();
    }

    @JsonSetter(value = "config")
    public Executor config(final Config config) {
        this.config = config;
        return this;
    }

    @JsonGetter("config")
    public Config getConfig() {
        return config;
    }
}
