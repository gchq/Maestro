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

package uk.gov.gchq.maestro.executor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
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
import uk.gov.gchq.maestro.executor.hook.Hook;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.executor.operation.handler.job.util.JobExecutor;
import uk.gov.gchq.maestro.executor.operation.validator.OperationValidation;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.executor.util.ExecutorPropertiesUtil;
import uk.gov.gchq.maestro.executor.util.Request;
import uk.gov.gchq.maestro.executor.util.Result;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.jobtracker.JobDetail;
import uk.gov.gchq.maestro.operation.jobtracker.JobStatus;
import uk.gov.gchq.maestro.operation.jobtracker.JobTracker;
import uk.gov.gchq.maestro.operation.user.User;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@Since("0.0.1")
@JsonPropertyOrder(value = {"class", "config"}, alphabetic = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
public class Executor implements Comparable<Executor>, Serializable {
    public static final String ERROR_DESERIALISE_EXECUTOR = "Could not deserialise Executor from given byte[]";
    public static final String INITIALISER = "initialiser";
    public static final String NO_HANDLER_WAS_FOUND_FOR_OPERATION = "Error in Executor: %s No handler was found for operation type: %s, this is an illegal state because a default handler should have been selected.";
    private static final long serialVersionUID = -5566921581366812872L;
    private Config config;
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    @JsonCreator
    public Executor(@JsonProperty("config") final Config config) {
        config(config);
    }

    protected void startCacheServiceLoader(final Map<String, Object> properties) {
        if (null != properties) {
            CacheServiceLoader.initialise(properties);
        }
    }

    private void addExecutorService(final Map<String, Object> properties) {
        if (null != properties) {
            ExecutorService.initialise(ExecutorPropertiesUtil.getJobExecutorThreadCount(this));
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
            throw new OperationException(e);
        }

        final Result<O> rtn = new Result(result, clonedRequest.getContext());
        LOGGER.debug("Rtn will returned: {}", rtn);
        return rtn;
    }

    /**
     * @param operation the operation class to check
     * @return true if the provided operation is supported.
     */
    public boolean isSupported(final Operation operation) {
        return isSupported(operation.getId());
    }

    public boolean isSupported(final String operationType) {
        return config.getOperationHandlers().containsKey(operationType);
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
        final OperationHandler operationHandler = getNonDefaultHandler(operation);
        return (isNull(operationHandler) ? config.getDefaultHandler() : operationHandler);
    }

    private OperationHandler getNonDefaultHandler(final Operation operation) {
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

    @JsonIgnore
    public Map<String, OperationHandler> getOperationHandlerMap() {
        return ImmutableMap.copyOf(config.getOperationHandlers());
    }

    @JsonIgnore
    public Set<String> getSupportedOperations() {
        return getOperationHandlerMap().keySet();
    }

    private Executor config(final Config config) {
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

    public Config addOperationHandler(final String opId, final OperationHandler handler) {
        return config.addOperationHandler(opId, handler);
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

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    @JsonGetter("config")
    public Config getConfig() {
        return config;
    }

    // @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    // @JsonGetter("config")
    // public Config getConfig() throws MaestroCheckedException {
    //TODO implement deep clone.
    // try {
    //     return JSONSerialiser.deserialise(JSONSerialiser.serialise(config), Config.class);
    // } catch (final Exception e) {
    //     throw new MaestroCheckedException("Error getting config from Executor: " + getId(), e);
    // }
    // }

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
                LOGGER.debug("operation: {} returned: {}", operation.getId(), result);
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
                throw new OperationException(e);
            }
        } else {
            throw new IllegalStateException(String.format(NO_HANDLER_WAS_FOUND_FOR_OPERATION, this.getId(), operation.getId()));
        }

        if (null == result) {
            CloseableUtil.close(operation);
        }
        LOGGER.debug("operation: {} returned: {}", operation.getId(), result);
        return result;
    }

    private void runInitOperation() throws OperationException {
        final Operation operation = new Operation(INITIALISER);
        final OperationHandler handler = getNonDefaultHandler(operation);
        final Context context = new Context();

        if (null != handler) {
            if (handler instanceof OperationValidation) {
                ((OperationValidation) handler).prepareOperation(operation,
                        context, this);
            }
            try {
                final Object o = handler.doOperation(operation, context, this);
                LOGGER.info("Initialise operation returned: {}", o);
            } catch (final Exception e) {
                LOGGER.error("InitialiseOp failed");
                throw e;
            }
        } else {
            LOGGER.info("No Initialiser Operation Handler supplied");
        }
    }

    @JsonIgnore
    public String getId() {
        return config.getId();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return new EqualsBuilder()
                .append(this.config, ((Executor) o).config)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(config)
                .toHashCode();
    }

    public Object getPropertyOrDefault(final String key, final Object defaultValue) {
        return config.getPropertyOrDefault(key, defaultValue); //TODO review usage and cast of this method
    }

    public Object getProperty(final String key) {
        return config.getProperty(key); //TODO review useage and cast of this method
    }

    public String setProperty(final String key, final String value) {
        return config.setProperty(key, value);
    }

    public String getDescription() {
        return config.getDescription();
    }

    @Override
    public int compareTo(final Executor that) {
        requireNonNull(that, "tried to compare null object");
        return new CompareToBuilder().append(this.config, that.config).toComparison();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("config", config)
                .toString();
    }
}
