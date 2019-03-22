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

import uk.gov.gchq.maestro.commonutil.CloseableUtil;
import uk.gov.gchq.maestro.commonutil.ExecutorService;
import uk.gov.gchq.maestro.commonutil.cache.CacheServiceLoader;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.exception.SerialisationException;
import uk.gov.gchq.maestro.jobtracker.JobDetail;
import uk.gov.gchq.maestro.jobtracker.JobStatus;
import uk.gov.gchq.maestro.jobtracker.JobTracker;
import uk.gov.gchq.maestro.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.DefaultOperation;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.impl.job.Job;
import uk.gov.gchq.maestro.operation.validator.OperationValidation;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Config;
import uk.gov.gchq.maestro.util.Request;
import uk.gov.gchq.maestro.util.Result;
import uk.gov.gchq.maestro.util.hook.Hook;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.Objects.nonNull;

@JsonPropertyOrder(value = {"class", "config"}, alphabetic = true)
public class Executor {
    public static final String OPERATION_S_IS_NOT_SUPPORTED_BY_THE_S = "Operation %s is not supported by the %s.";
    public static final String ERROR_DESERIALISE_EXECUTOR = "Could not deserialise Executor from given byte[]";
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    private Config config;
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    public Executor() {
        this(new Config());
    }

    public Executor(final Config config) {
        this.config = config;
        startCacheServiceLoader(config.getProperties());
        addExecutorService(config.getProperties());
    }

    protected void startCacheServiceLoader(final StoreProperties properties) {
        CacheServiceLoader.initialise(properties.getProperties());
    }

    private void addExecutorService(final StoreProperties properties) {
        ExecutorService.initialise(properties.getJobExecutorThreadCount());
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

    public static Executor deserialise(final String jsonString) {
        try {
            LOGGER.info("Deserialising Executor from byte[]");
            return JSONSerialiser.deserialise(jsonString, Executor.class);
        } catch (final SerialisationException e) {
            LOGGER.error(ERROR_DESERIALISE_EXECUTOR);
            throw new IllegalArgumentException(ERROR_DESERIALISE_EXECUTOR, e);
        }
    }

    public <O> O execute(final Operation operation, final Context context) {
        return (O) execute(new Request(operation, context)).getResult();
    }

    public <O> O execute(final Operation operation, final User user) {
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


    /**
     * DO NOT USE
     *
     * @param operation the operation to execute.
     * @param context   the context executing the job.
     * @return the job detail.
     * @throws OperationException thrown if jobs are not configured.
     * @deprecated This is only here so if the executejob endpoint is hit it
     * will just wrap it in a Job Op and pass it to the executor, DO NOT USE.
     * Executes a given operation job and returns the job detail.
     */
    // TODO remove this method before first release
    public JobDetail executeJob(final Operation operation, final Context context) {
        return execute(new Job.Builder().operation(operation).build(), context);
    }

    /**
     * @param operationClass the operation class to check
     * @return true if the provided operation is supported.
     */
    public boolean isSupported(final Class<? extends Operation> operationClass) {
        return null != getConfig().getOperationHandler(operationClass);
    }

    public void runAsync(final Runnable runnable) {
        getExecutorService().execute(runnable);
    }

    @JsonIgnore
    public ScheduledExecutorService getExecutorService() {
        return (null != ExecutorService.getService() && ExecutorService.isEnabled()) ?
                ExecutorService.getService() : null;
    }

    @JsonIgnore
    private OperationHandler<? extends Operation> getHandler(final Class<?
            extends Operation> opClass) {
        return config.getOperationHandler(opClass);
    }

    @JsonIgnore
    public Map<Class<? extends Operation>, OperationHandler> getOperationHandlerMap() {
        return ImmutableMap.copyOf(config.getOperationHandlers());
    }

    public Executor operationHandlerMap(final Map<Class<? extends Operation>, OperationHandler> operationHandlerMap) {
        this.config.getOperationHandlers().clear();
        if (nonNull(operationHandlerMap)) {
            this.config.getOperationHandlers().putAll(operationHandlerMap);
        }
        return this;
    }

    public JobDetail addOrUpdateJobDetail(final Operation operation,
                                          final Context context, final String msg, final JobStatus jobStatus) {
        final JobDetail newJobDetail = new JobDetail(context.getJobId(), context
                .getUser()
                .getUserId(), OperationChain.wrap(operation), jobStatus, msg);
        if (JobTracker.isCacheEnabled()) {
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

    @JsonGetter("class") //TODO improvement
    public String getClassName() {
        return this.getClass().getCanonicalName();
    }

    @JsonSetter(value = "config")
    public Executor config(final Config config) {
        if (nonNull(config)) {
            this.config = config;
        }
        return this;
    }

    @JsonGetter("config")
    public Config getConfig() {
        return config;
    }

    private Object handleOperation(final Operation operation,
                                   final Context context) throws OperationException {
        Object result;
        final OperationHandler handler = getHandler(operation.getClass());

        if (null != handler) {
            if (handler instanceof OperationValidation) {
                ((OperationValidation) handler).prepareOperation(operation,
                        context, this);
            }
            result = handler.doOperation(operation, context, this);
        } else if (operation instanceof DefaultOperation) {
            result = doUnhandledOperation(operation);
        } else {
            final Operation defaultOp = new DefaultOperation().setWrappedOp(operation);
            result = this.handleOperation(defaultOp, context);
        }

        if (null == result) {
            CloseableUtil.close(operation);
        }

        return result;
    }

    private Object doUnhandledOperation(final Operation operation) {
        throw new UnsupportedOperationException(String.format(OPERATION_S_IS_NOT_SUPPORTED_BY_THE_S, operation.getClass(), this.getClass().getSimpleName()));
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
}
