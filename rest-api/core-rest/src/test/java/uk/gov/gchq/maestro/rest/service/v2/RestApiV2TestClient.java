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

import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.rest.RestApiTestClient;
import uk.gov.gchq.maestro.rest.SystemStatus;
import uk.gov.gchq.maestro.rest.application.ApplicationConfigV2;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import java.io.IOException;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class RestApiV2TestClient extends RestApiTestClient {

    public static final String HTTP_LOCALHOST_8080 = "http://localhost:8080/";
    public static final String REST = "rest/";
    public static final String VERSION = ApplicationConfigV2.VERSION;

    public RestApiV2TestClient() {
        super(HTTP_LOCALHOST_8080, REST, VERSION, new ApplicationConfigV2());
    }

    @Override
    public Response executeOperation(final Operation operation) throws IOException {
        startServer();
        return client.target(uriString)
                .path("/executor/operations/execute")
                .request()
                .post(Entity.entity(JSONSerialiser.serialise(operation), APPLICATION_JSON_TYPE));
    }

    @Override
    public Response executeOperationChain(final OperationChain opChain) throws IOException {
        startServer();
        return client.target(uriString)
                .path("/executor/operations/execute")
                .request()
                .post(Entity.entity(JSONSerialiser.serialise(opChain), APPLICATION_JSON_TYPE));
    }

    @Override
    public Response executeOperationChainChunked(final OperationChain opChain) throws IOException {
        return executeOperationChunked(opChain);
    }

    @Override
    public Response executeOperationChunked(final Operation operation) throws IOException {
        startServer();
        return client.target(uriString)
                .path("/executor/operations/execute/chunked")
                .request()
                .post(Entity.entity(JSONSerialiser.serialise(operation), APPLICATION_JSON_TYPE));
    }

    @Override
    public SystemStatus getRestServiceStatus() {
        return client.target(uriString)
                .path("/executor/status")
                .request()
                .get(SystemStatus.class);
    }

    @Override
    public Response getOperationDetails(final Class clazz) throws IOException {
        return client.target(uriString)
                .path("executor/operations/" + clazz.getCanonicalName())
                .request()
                .get(Response.class);
    }
}
