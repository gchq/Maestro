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

package uk.gov.gchq.maestro.rest.service.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.commonutil.CloseableUtil;
import uk.gov.gchq.maestro.commonutil.ToStringBuilder;
import uk.gov.gchq.maestro.commonutil.exception.Error;
import uk.gov.gchq.maestro.commonutil.exception.MaestroRuntimeException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.exception.Status;
import uk.gov.gchq.maestro.commonutil.pair.Pair;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JsonSerialisationUtil;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.executor.util.Request;
import uk.gov.gchq.maestro.executor.util.Result;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.rest.factory.ExecutorFactory;
import uk.gov.gchq.maestro.rest.factory.UserFactory;
import uk.gov.gchq.maestro.rest.service.v2.example.ExamplesFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static uk.gov.gchq.maestro.rest.ServiceConstants.JOB_ID_HEADER;
import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE;
import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE_HEADER;

/**
 * An implementation of {@link IOperationServiceV2}. By default it will use a singleton
 * {@link Executor} generated using the {@link ExecutorFactory}.
 * All operations are simple delegated to the executor.
 * Pre and post operation hooks are available by extending this class and implementing preOperationHook and/or
 * postOperationHook.
 */
public class OperationServiceV2 implements IOperationServiceV2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationServiceV2.class);
    public static final String OUTPUT_TYPE_REFERENCE = "outputTypeReference"; //TODO outputTypeReference, review move to operationHandler

    @Inject
    private ExecutorFactory executorFactory;

    @Inject
    private UserFactory userFactory;

    @Inject
    private ExamplesFactory examplesFactory;

    public final ObjectMapper mapper = JSONSerialiser.createDefaultMapper();

    @Override
    public Response getOperations() {
        return Response.ok(executorFactory.getExecutor().getSupportedOperations())
                .header(MAESTRO_MEDIA_TYPE_HEADER, MAESTRO_MEDIA_TYPE)
                .build();
    }

    @Override
    public Response getOperationDetails() {
        final Executor executor = executorFactory.getExecutor();
        Set<String> supportedOperations = executor.getSupportedOperations();
        List<OperationDetail> supportedClassesAsOperationDetail = new ArrayList<>();

        for (final String supportedOperation : supportedOperations) {
            supportedClassesAsOperationDetail.add(new OperationDetail(supportedOperation, executor));
        }

        return Response.ok(supportedClassesAsOperationDetail)
                .header(MAESTRO_MEDIA_TYPE_HEADER, MAESTRO_MEDIA_TYPE)
                .build();
    }

    @Override
    public Response execute(final Operation operation) {
        final Pair<Object, String> resultAndJobId = _execute(operation, userFactory.createContext());
        final HashMap<String, Object> newResult = new HashMap<>(); //TODO Demo cheat high priority
        newResult.put("result", resultAndJobId.getFirst());
        final Response rtn = Response.ok(newResult)
                .header(MAESTRO_MEDIA_TYPE_HEADER, MAESTRO_MEDIA_TYPE)
                .header(JOB_ID_HEADER, resultAndJobId.getSecond())
                .build();
        LOGGER.debug("Response = {}, entity = {}", rtn, rtn.getEntity());
        return rtn;
    }

    @Override
    public Response operationDetails(final String operationType) {
        final Executor executor = executorFactory.getExecutor();
        if (executor.isSupported(operationType)) {
            return Response.ok(new OperationDetail(operationType, executor))
                    .header(MAESTRO_MEDIA_TYPE_HEADER, MAESTRO_MEDIA_TYPE)
                    .build();
        } else {
            LOGGER.info("Operation: {} was not explicitly supported by the executor.", operationType);
            return Response.status(NOT_FOUND)
                    .entity(new Error.ErrorBuilder()
                            .status(Status.NOT_FOUND)
                            .statusCode(404)
                            .simpleMessage("Operation: " + operationType + " is not supported by the current executor.")
                            .detailMessage("Operation: " + operationType + " was found in the supportedOperations Map")
                            .build())
                    .header(MAESTRO_MEDIA_TYPE_HEADER, MAESTRO_MEDIA_TYPE)
                    .build();
        }
    }

    @Override
    public Response operationExample(final String operationType) throws InstantiationException, IllegalAccessException {
        try {
            return Response.ok(generateExampleJson(operationType))
                    .header(MAESTRO_MEDIA_TYPE_HEADER, MAESTRO_MEDIA_TYPE)
                    .build();
        } catch (final Exception e) {
            LOGGER.info("Unable to create example JSON for class: {}.", operationType, e);
            throw e;
            /*
            return Response.status(TODO review Is a response more suitable?)
                    .header(MAESTRO_MEDIA_TYPE_HEADER, MAESTRO_MEDIA_TYPE)
                    .build();
             */
        }
    }

    protected void preOperationHook(final OperationChain opChain, final Context context) {
        // no action by default
    }

    protected void postOperationHook(final OperationChain opChain, final Context context) {
        // no action by default
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    protected <O> Pair<O, String> _execute(final Operation operation, final Context context) {

        OperationChain opChain = OperationChain.wrap(operation.getId(), operation);
        preOperationHook(opChain, context);

        Result<O> result;
        try {
            result = executorFactory.getExecutor().execute(new Request(opChain, context));
        } catch (final OperationException e) {
            CloseableUtil.close(operation);
            if (null != e.getMessage()) {
                throw new RuntimeException("Error executing opChain: " + e.getMessage(), e);
            } else {
                throw new RuntimeException("Error executing opChain", e);
            }
        } finally {
            try {
                postOperationHook(opChain, context);
            } catch (final Exception e) {
                CloseableUtil.close(operation);
                throw e;
            }
        }

        final Pair<O, String> rtn = new Pair<>(result.getResult(), result.getContext().getJobId());
        LOGGER.debug("will rtn: {}", rtn);
        return rtn;
    }

    protected void chunkResult(final Object result, final ChunkedOutput<String> output) {
        if (result instanceof Iterable) {
            final Iterable itr = (Iterable) result;
            try {
                for (final Object item : itr) {
                    output.write(mapper.writeValueAsString(item));
                }
            } catch (final IOException ioe) {
                LOGGER.warn("IOException (chunks)", ioe);
            } finally {
                CloseableUtil.close(itr);
            }
        } else {
            try {
                output.write(mapper.writeValueAsString(result));
            } catch (final IOException ioe) {
                LOGGER.warn("IOException (chunks)", ioe);
            }
        }
    }

    private Operation generateExampleJson(final String operationType) {
        return examplesFactory.generateExample(operationType);
    }


    private List<OperationField> getOperationFields(final Operation operation, final Executor executor) {

        if (!executor.isSupported(operation)) {
            throw new MaestroRuntimeException(String.format("Operation: %s not explicitly supported by executor", operation.getId()));
        }

        final List<OperationField> rtn = new ArrayList<>();

        final FieldDeclaration fieldDeclaration = executor.getFieldDeclaration(operation);

        final TreeMap<String, Class> fieldDeclarations = fieldDeclaration.getFields();
        if (Objects.nonNull(fieldDeclarations)) {
            for (final Map.Entry<String, Class> entry : fieldDeclarations.entrySet()) {
                final boolean required = !fieldDeclaration.optionalContains(entry.getKey());
                final String summary = "summary TBA"; //TODO OperationField summary
                final String name = entry.getKey();
                final String className = entry.getValue().getCanonicalName();
                rtn.add(new OperationField(name, summary, className, null, required));
            }
        }

        return rtn;
    }

    private String getOperationOutputType(final Operation operation) {
        String outputClass = null;
        if (operation.containsKey(OUTPUT_TYPE_REFERENCE)) {
            final Object outputTypeReference = operation.get(OUTPUT_TYPE_REFERENCE);
            //TODO? just store the object required in the Operation map?
            outputClass = JsonSerialisationUtil.getTypeString((Type) outputTypeReference);
        }
        return outputClass;
    }

    /**
     * POJO to store details for a single user defined field in an {@link Operation}.
     */
    private class OperationField {
        private final String name;
        private final String summary;
        private String className;
        private Set<String> options;
        private final boolean required;

        OperationField(final String name, final String summary, final String className, final Set<String> options, final boolean required) {
            this.name = name;
            this.summary = summary;
            this.className = className;
            this.options = options;
            this.required = required;
        }

        public String getName() {
            return name;
        }

        public String getSummary() {
            return summary;
        }

        public String getClassName() {
            return className;
        }

        public Set<String> getOptions() {
            return options;
        }

        public boolean isRequired() {
            return required;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final OperationField that = (OperationField) o;

            return new EqualsBuilder()
                    .append(required, that.required)
                    .append(name, that.name)
                    .append(summary, that.summary)
                    .append(className, that.className)
                    .append(options, that.options)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(name)
                    .append(summary)
                    .append(className)
                    .append(options)
                    .append(required)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("operationType", name)
                    .append("summary", summary)
                    .append("className", className)
                    .append("options", options)
                    .append("required", required)
                    .toString();
        }
    }

    /**
     * POJO to store details for a user specified {@link Operation}
     * class.
     */
    protected class OperationDetail {
        private final String operationType;
        private final List<OperationField> fields;
        private final Operation exampleJson;
        private final String outputClassName;

        OperationDetail(final String operationType, final Executor executor) {
            this.operationType = operationType;
            this.exampleJson = generateExampleJson(operationType);
            this.fields = getOperationFields(exampleJson, executor);
            this.outputClassName = getOperationOutputType(exampleJson);
        }

        public String getOperationType() {
            return operationType;
        }

        public List<OperationField> getFields() {
            return fields;
        }

        public Operation getExampleJson() {
            return exampleJson;
        }

        public String getOutputClassName() {
            return outputClassName;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final OperationDetail that = (OperationDetail) o;

            return new EqualsBuilder()
                    .append(operationType, that.operationType)
                    .append(fields, that.fields)
                    .append(exampleJson, that.exampleJson)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(operationType)
                    .append(fields)
                    .append(exampleJson)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("operationType", operationType)
                    .append("fields", fields)
                    .append("exampleJson", exampleJson)
                    .toString();
        }
    }
}
