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
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.handler.InitialiserHandler;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;
import uk.gov.gchq.maestro.util.ExecutorPropertiesUtil;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Properties;

@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class ProxyInitialiseHandler implements InitialiserHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyInitialiseHandler.class);

    private Client client;

    @Override
    public Object _doOperation(final Operation ignore, final Context context, final Executor executor) throws OperationException {
        client = ProxyUtil.createClient(executor);
        checkDelegateStoreStatus(executor);
        return null;
    }


    protected void checkDelegateStoreStatus(final Executor executor) throws OperationException { //TODO rename
        final URL url = ExecutorPropertiesUtil.getMaestroUrl(executor, "/executor/status");
        final LinkedHashMap status = doGet(url, new TypeReferenceImpl.Map(), null);
        LOGGER.info("ProxyUtil REST API status: {}", status.get("description"));
    }

    protected <O> O doGet(final URL url,
                          final TypeReference<O> outputTypeReference, final Context context)
            throws OperationException {
        final Invocation.Builder request = ProxyUtil.createRequest(null, url, context, client);
        final Response response;
        try {
            response = request.get();
        } catch (final Exception e) {
            throw new OperationException("Request failed to execute via url "
                    + url.toExternalForm(), e);
        }

        return ProxyUtil.handleResponse(response, outputTypeReference, LOGGER);
    }


    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final ProxyInitialiseHandler that = (ProxyInitialiseHandler) o;

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
