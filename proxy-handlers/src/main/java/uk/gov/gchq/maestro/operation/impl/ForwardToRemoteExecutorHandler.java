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

package uk.gov.gchq.maestro.operation.impl;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.CommonConstants;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.util.ExecutorPropertiesUtil;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class ForwardToRemoteExecutorHandler implements OperationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForwardToRemoteExecutorHandler.class);
    public static final String OUTPUT_TYPE_REFERENCE = "outputTypeReference";
    private Client client;


    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        client = ProxyUtil.createClient(executor.getConfig().getProperties());
        final Properties properties = executor.getConfig().getProperties();

        return executeOpChainViaUrl(properties, operation, context);
    }


    public Object executeOpChainViaUrl(final Properties properties, final Operation operation, final Context context) throws OperationException {
        final String opChainJson;
        try {
            opChainJson = new String(JSONSerialiser.serialise(operation), CommonConstants.UTF_8);
        } catch (final UnsupportedEncodingException | SerialisationException e) {
            throw new OperationException("Unable to serialise operation chain into JSON.", e);
        }


        final URL url = (URL) ExecutorPropertiesUtil.getMaestroUrl(properties, "executor/operations/execute");
        try {
            return doPost(url, opChainJson, (TypeReference) operation.get(OUTPUT_TYPE_REFERENCE), context);
        } catch (final OperationException e) {
            throw new OperationException(e.getMessage(), e);
        }
    }

    protected <O> O doPost(final URL url, final String jsonBody,
                           final TypeReference<O> clazz,
                           final Context context) throws OperationException {

        final Invocation.Builder request = ProxyUtil.createRequest(jsonBody, url, context, client);
        final Response response;
        try {
            response = request.post(Entity.json(jsonBody));
        } catch (final Exception e) {
            throw new OperationException("Failed to execute post via " +
                    "the maestro URL " + url.toExternalForm(), e);
        }

        return ProxyUtil.handleResponse(response, clazz, LOGGER);
    }


    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration();
                // .fieldRequired(OUTPUT_TYPE_REFERENCE, TypeReference.class);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final ForwardToRemoteExecutorHandler that = (ForwardToRemoteExecutorHandler) o;

        return new EqualsBuilder()
                //Don't compare client
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                //Don't compare client
                .toHashCode();
    }
}
