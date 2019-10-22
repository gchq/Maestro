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

package uk.gov.gchq.maestro.proxy.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;

import uk.gov.gchq.maestro.commonutil.CommonConstants;
import uk.gov.gchq.maestro.commonutil.StringUtil;
import uk.gov.gchq.maestro.commonutil.exception.Error;
import uk.gov.gchq.maestro.commonutil.exception.MaestroWrappedErrorRuntimeException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.util.ExecutorPropertiesUtil;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.UnsupportedEncodingException;
import java.net.URL;

public final class ProxyUtil {

    private ProxyUtil() {
    }

    protected static Client createClient(final Executor executor) {
        final Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, ExecutorPropertiesUtil.getConnectTimeout(executor));
        client.property(ClientProperties.READ_TIMEOUT, ExecutorPropertiesUtil.getReadTimeout(executor));
        return client;
    }

    protected static <O> O handleResponse(final Response response,
                                          final TypeReference<O> outputTypeReference, final Logger logger)
            throws OperationException {
        final String outputJson = response.hasEntity() ? response.readEntity(String.class) : null;
        if (Response.Status.Family.SUCCESSFUL != response.getStatusInfo().getFamily()) {
            final Error error;
            try {
                error = JSONSerialiser.deserialise(StringUtil.toBytes(outputJson), Error.class);
            } catch (final Exception e) {
                logger.warn("Maestro bad status {}. Detail: {}", response.getStatus(), outputJson);
                throw new OperationException("ProxyUtil Executor returned status: " + response.getStatus() + ". Response content was: " + outputJson);
            }
            throw new MaestroWrappedErrorRuntimeException(error);
        }

        O output = null;
        if (null != outputJson) {
            try {
                output = deserialise(outputJson, outputTypeReference); //TODO outputTypeReference high priority
            } catch (final SerialisationException e) {
                throw new OperationException(e.getMessage(), e);
            }
        }

        return output;
    }

    protected static Invocation.Builder createRequest(final String body, final URL url, final Context context, final Client client) {
        final Invocation.Builder request = client.target(url.toString())
                .request();
        if (null != body) {
            request.header("Content", MediaType.APPLICATION_JSON_TYPE);
            request.build(body);
        }
        return request;
    }

    protected static <O> O deserialise(final String jsonString,
                                       final TypeReference<O> outputTypeReference)
            throws SerialisationException {
        final byte[] jsonBytes;
        try {
            jsonBytes = jsonString.getBytes(CommonConstants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            throw new SerialisationException(
                    "Unable to deserialise JSON: " + jsonString, e);
        }

        return JSONSerialiser.deserialise(jsonBytes, outputTypeReference);
    }
}
