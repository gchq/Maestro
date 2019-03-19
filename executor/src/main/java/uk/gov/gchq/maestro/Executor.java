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

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.jobtracker.JobDetail;
import uk.gov.gchq.maestro.jobtracker.JobStatus;
import uk.gov.gchq.maestro.jobtracker.JobTracker;
import uk.gov.gchq.maestro.operation.DefaultOperation;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Config;
import uk.gov.gchq.maestro.util.Hook;
import uk.gov.gchq.maestro.util.Request;
import uk.gov.gchq.maestro.util.Result;

import java.util.Map;

import static java.util.Objects.nonNull;


@JsonPropertyOrder(value = {"class", "operationHandlerMap", "config"}, alphabetic = true)
public class Executor {
    public static final String OPERATION_S_IS_NOT_SUPPORTED_BY_THE_S = "DoGetOperation %s is not supported by the %s.";
    public static final String ERROR_DESERIALISE_EXECUTOR = "Could not deserialise Executor from given byte[]";
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
    private Config config;
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    // This is just as a note to remind that this must be removed and added
    // as a util
    //private JobTracker jobTracker;

    public Executor() {
        this.config = new Config();
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


    @JsonCreator
    public Executor(@JsonProperty("operationHandlerMap") final Map<Class<?
            extends Operation>, OperationHandler> operationHandlerMap,
                    @JsonProperty("config") final Map<String, String> config) {
        this();
        if (nonNull(operationHandlerMap) && !operationHandlerMap.isEmpty()) {
            this.config.getOperationHandlers().putAll(operationHandlerMap);
        }
    }


    public <O> O execute(final Operation operation,
                         final Context context) throws OperationException {
        return (O) execute(new Request(operation, context)).getResult();
    }

    public <O> O execute(final Operation operation,
                         final User user) throws OperationException {
        return (O) execute(new Request(operation, new Context(user))).getResult();
    }

    /**
     * Executes a given operation and returns the result.
     *
     * @param request the request to execute.
     * @param <O>     the output type of the operation
     * @return the result of executing the operation
     * @throws OperationException thrown by the operation handler if the
     *                            operation fails.
     */
    public <O> Result<O> execute(final Request request) {
        if (null == request) {
            throw new IllegalArgumentException("A request is required");
        }

        if (null == request.getContext()) {
            throw new IllegalArgumentException("A context is required");
        }

        request.setConfig(config);
        request.getContext().setOriginalOperation(request.getOperation());
        final Request clonedRequest = request.fullClone();
        final Operation operation = clonedRequest.getOperation();
        final Context context = clonedRequest.getContext();

        addOrUpdateJobDetail(operation, context, null, JobStatus.RUNNING);
        O result = null;
        try {
            for (final Hook graphHook : getConfig().getHooks()) {
                graphHook.preExecute(clonedRequest);
            }
            result = (O) handleOperation(operation, context);
            for (final Hook graphHook : getConfig().getHooks()) {
                result = graphHook.postExecute(result,
                        clonedRequest);
            }
            addOrUpdateJobDetail(operation, context, null, JobStatus.FINISHED);
        } catch (final Exception e) {
            for (final Hook graphHook : getConfig().getHooks()) {
                try {
                    result = graphHook.onFailure(result,
                            clonedRequest, e);
                } catch (final Exception graphHookE) {
                    LOGGER.warn("Error in graphHook " + graphHook.getClass().getSimpleName() + ": " + graphHookE.getMessage(), graphHookE);
                }
            }
        } catch (final Throwable t) {
            addOrUpdateJobDetail(operation, context, t.getMessage(), JobStatus.FAILED);
            throw t;
        }
        return new Result(result, clonedRequest.getContext());
    }


    private Object handleOperation(final Operation operation,
                                   final Context context) {
        Object result;
        final OperationHandler<Operation> handler = getHandler(operation.getClass());

        if (null != handler) {
            result = handler.doOperation(operation, context, this);
        } else if (operation instanceof DefaultOperation) {
            result = doUnhandledOperation(operation);
        } else {
            final Operation defaultOp = new DefaultOperation().setWrappedOp(operation);
            result = this.handleOperation(defaultOp, context);
        }

        return result;
    }

    private <O, Op extends Operation> O doUnhandledOperation(final Op operation) {
        throw new UnsupportedOperationException(String.format(OPERATION_S_IS_NOT_SUPPORTED_BY_THE_S, operation.getClass(), this.getClass().getSimpleName()));
    }

    @JsonIgnore
    private OperationHandler<Operation> getHandler(final Class<? extends Operation> opClass) {
        return config.getOperationHandler(opClass);
    }

    public Map<Class<? extends Operation>, OperationHandler> getOperationHandlerMap() {
        return ImmutableMap.copyOf(config.getOperationHandlers());
    }

    public Executor operationHandlerMap(final Map<Class<? extends Operation>, OperationHandler> operationHandlerMap) {
        this.config.getOperationHandlers().clear();
        this.config.getOperationHandlers().putAll(operationHandlerMap);
        return this;
    }

    private JobDetail addOrUpdateJobDetail(final Operation operation,
                                           final Context context, final String msg, final JobStatus jobStatus) {
        final JobDetail newJobDetail = new JobDetail(context.getJobId(), context
                .getUser()
                .getUserId(), OperationChain.wrap(operation), jobStatus, msg);
        if (JobTracker.isJobTrackerCacheEnabled()) {
            final JobDetail oldJobDetail =
                    JobTracker.getJob(newJobDetail.getJobId(),
                            context
                                    .getUser());
            if (null == oldJobDetail) {
                JobTracker.addOrUpdateJob(newJobDetail, context.getUser());
            } else {
                JobTracker.addOrUpdateJob(new JobDetail(oldJobDetail, newJobDetail), context
                        .getUser());
            }
        }
        return newJobDetail;
    }

    public Config getConfig() {
        return config;
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

    public Executor config(final Config config) {
        this.config = config;
        return this;
    }
}
