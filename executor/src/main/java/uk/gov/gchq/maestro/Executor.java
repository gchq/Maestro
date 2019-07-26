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

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.maestro.commonutil.CloseableUtil;
import uk.gov.gchq.maestro.commonutil.ExecutorService;
import uk.gov.gchq.maestro.commonutil.cache.CacheServiceLoader;
import uk.gov.gchq.maestro.commonutil.exception.ExecutorException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.hook.Hook;
import uk.gov.gchq.maestro.jobtracker.JobDetail;
import uk.gov.gchq.maestro.jobtracker.JobStatus;
import uk.gov.gchq.maestro.jobtracker.JobTracker;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.handler.job.util.JobExecutor;
import uk.gov.gchq.maestro.operation.validator.OperationValidation;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Config;
import uk.gov.gchq.maestro.util.ExecutorPropertiesUtil;
import uk.gov.gchq.maestro.util.Request;
import uk.gov.gchq.maestro.util.Result;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Since("0.0.1")
@JsonPropertyOrder(value = {"class", "config"}, alphabetic = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
public class Executor {
    public static final String ERROR_DESERIALISE_EXECUTOR = "Could not deserialise Executor from given byte[]";
    public static final String DEFAULT_OPERATION = "DefaultOperation";
    public static final String WRAPPED_OP = "WrappedOp";
    public static final String INITIALISER = "Initialiser";
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    private Config config;
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    public Executor() {
        this(new Config());
    }

    public Executor(final Config config) {
        config(config);
    }

    protected void startCacheServiceLoader(final Properties properties) {
        if (null != properties) {
            CacheServiceLoader.initialise(properties);
        }
    }

    private void addExecutorService(final Properties properties) {
        if (null != properties) {
            ExecutorService.initialise(ExecutorPropertiesUtil.getJobExecutorThreadCount(properties));
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

    public static Executor deserialise(final String jsonString) {
        try {
            LOGGER.info("Deserialising Executor from byte[]");
            return JSONSerialiser.deserialise(jsonString, Executor.class);
        } catch (final SerialisationException e) {
            LOGGER.error(ERROR_DESERIALISE_EXECUTOR);
            throw new IllegalArgumentException(ERROR_DESERIALISE_EXECUTOR, e);
        }
    }

    public <O> O execute(final Operation operation, final Context context) throws OperationException {
        return (O) execute(new Request(operation, context)).getResult();
    }

    public <O> O execute(final Operation operation, final User user) throws OperationException {
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
    public <O> Result<O> execute(final Request request) throws OperationException {
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

        O result = null;
        try {
            for (final Hook requestHook : getConfig().getRequestHooks()) {
                requestHook.preExecute(clonedRequest);
            }
            result = (O) handleOperation(operation, context);
            for (final Hook requestHook : getConfig().getRequestHooks()) {
                result = requestHook.postExecute(result, clonedRequest);
            }
        } catch (final Exception e) {
            for (final Hook requestHook : getConfig().getRequestHooks()) {
                try {
                    result = requestHook.onFailure(result, clonedRequest, e);
                } catch (final Exception requestHookE) {
                    LOGGER.warn("Error in requestHook " + requestHook.getClass().getSimpleName() + ": " + requestHookE.getMessage(), requestHookE);
                }
            }
            CloseableUtil.close(operation);
            CloseableUtil.close(result);
            throw e;
        }
        return new Result(result, clonedRequest.getContext());
    }

    /**
     * @param operation the operation class to check
     * @return true if the provided operation is supported.
     */
    public boolean isSupported(final Operation operation) {
        return isSupported(operation.getId());
    }

    public boolean isSupported(final String operationType) {
        return getOperationHandlerMap().containsKey(operationType);
    }

    public void runAsync(final Runnable runnable) {
        getExecutorService().execute(runnable);
    }

    @JsonIgnore
    public ScheduledExecutorService getExecutorService() {
        return (null != ExecutorService.getService() && ExecutorService.isEnabled()) ?
                ExecutorService.getService() : null;
    }

    private OperationHandler getHandler(final Operation operation) {
        return config.getOperationHandler(operation);
    }


    public FieldDeclaration getFieldDeclaration(final Operation operation) {
        final FieldDeclaration rtn;
        final OperationHandler handler = getHandler(operation);
        if (nonNull(handler)) {
            rtn = handler.getFieldDeclaration();
        } else {
            rtn = new FieldDeclaration();
        }
        return rtn;
    }

    public Executor addHandler(final String operationID, final OperationHandler handler) {
        config.getOperationHandlers().put(operationID, handler);
        return this;
    }

    @JsonIgnore
    public Map<String, OperationHandler> getOperationHandlerMap() {
        return ImmutableMap.copyOf(config.getOperationHandlers());
    }

    @JsonIgnore
    public Set<String> getSupportedOperations() {
        return getOperationHandlerMap().keySet();
    }

    public Executor operationHandlerMap(final Map<String, OperationHandler> operationHandlerMap) {
        this.config.getOperationHandlers().clear();
        if (nonNull(operationHandlerMap)) {
            this.config.getOperationHandlers().putAll(operationHandlerMap);
        }
        return this;
    }

    @JsonSetter(value = "config")
    public Executor config(final Config config) {
        if (nonNull(config)) {
            this.config = config;
            startCacheServiceLoader(config.getProperties());
            addExecutorService(config.getProperties());
            try {
                startScheduledJobs();
                runInitOperation();
            } catch (final Exception e) {
                throw new ExecutorException(e);
            }
        } else {
            throw new ExecutorException(new IllegalArgumentException("Config is null"));
        }
        return this;
    }

    public void startScheduledJobs() throws OperationException {
        if (JobTracker.isCacheEnabled() && ExecutorService.isEnabled()) {
            for (final JobDetail jobDetailFromCache : JobTracker.getAllJobs()) {
                if (jobDetailFromCache.getStatus().equals(JobStatus.SCHEDULED_PARENT)) {
                    JobExecutor.executeJob(jobDetailFromCache, this);
                }
            }
        }
    }

    @JsonGetter("config")
    public Config getConfig() {
        return config;
    }

    private Object handleOperation(final Operation operation,
                                   final Context context) throws OperationException {
        Object result = null;
        final OperationHandler handler = getHandler(operation);
        final Request opAsRequest = new Request(operation, context);

        if (null != handler) {
            if (handler instanceof OperationValidation) {
                ((OperationValidation) handler).prepareOperation(operation,
                        context, this);
            }
            try {
                for (final Hook operationHook : getConfig().getOperationHooks()) {
                    operationHook.preExecute(opAsRequest);
                }
                result = handler.doOperation(operation, context, this);
                for (final Hook operationHook : getConfig().getRequestHooks()) {
                    result = operationHook.postExecute(result, opAsRequest);
                }
            } catch (final Exception e) {
                for (final Hook operationHook : getConfig().getRequestHooks()) {
                    try {
                        result = operationHook.onFailure(result, opAsRequest, e);
                    } catch (final Exception operationHookE) {
                        LOGGER.warn("Error in operationHook " + operationHook.getClass().getSimpleName() + ": " + operationHookE.getMessage(), operationHookE);
                    }
                }
                throw e;
            }
        } else if (operation.getIdComparison(DEFAULT_OPERATION)) {
            /*Id: DEFAULT_OPERATION is acceptable because it is defined below by this handler.*/
            result = doUnhandledOperation(operation);
        } else {
            final Operation defaultOp = new Operation(DEFAULT_OPERATION)
                    .operationArg(WRAPPED_OP, operation);
            result = this.handleOperation(defaultOp, context);
        }

        if (null == result) {
            CloseableUtil.close(operation);
        }

        return result;
    }

    private void runInitOperation() throws OperationException {
        final Operation operation = new Operation("Initialiser");
        final OperationHandler handler = getHandler(operation);
        final Context context = new Context();

        if (null != handler) {
            if (handler instanceof OperationValidation) {
                ((OperationValidation) handler).prepareOperation(operation,
                        context, this);
            }
            try {
                handler.doOperation(operation, context, this);
            } catch (final Exception e) {
                LOGGER.error("InitialiseOp failed");
                throw e;
            }
        } else {
            LOGGER.debug("No Initialiser Operation Handler supplied");
        }
    }

    private Object doUnhandledOperation(final Operation operation) {
        final Class<? extends Operation> aClass = operation.getClass();
        CloseableUtil.close(operation);
        final String simpleName = this.getConfig().getId();
        throw new UnsupportedOperationException(format("Operation %s is not supported by executor: %s. WrappedOp: %s", aClass, simpleName, operation.get(WRAPPED_OP)));
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
