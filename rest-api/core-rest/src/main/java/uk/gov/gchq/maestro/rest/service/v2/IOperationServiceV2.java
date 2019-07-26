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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static uk.gov.gchq.maestro.rest.ServiceConstants.BAD_REQUEST;
import static uk.gov.gchq.maestro.rest.ServiceConstants.FORBIDDEN;
import static uk.gov.gchq.maestro.rest.ServiceConstants.INTERNAL_SERVER_ERROR;
import static uk.gov.gchq.maestro.rest.ServiceConstants.JOB_ID_HEADER;
import static uk.gov.gchq.maestro.rest.ServiceConstants.JOB_ID_HEADER_DESCRIPTION;
import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE_HEADER;
import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE_HEADER_DESCRIPTION;
import static uk.gov.gchq.maestro.rest.ServiceConstants.OK;
import static uk.gov.gchq.maestro.rest.ServiceConstants.OPERATION_NOT_FOUND;
import static uk.gov.gchq.maestro.rest.ServiceConstants.OPERATION_NOT_IMPLEMENTED;

/**
 * An {@code IOperationServiceV2} has methods to execute {@link Operation}s on the
 * {@link uk.gov.gchq.maestro.Executor}.
 */
@Path("/executor/operations")
@Api(value = "operations")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface IOperationServiceV2 {

    @GET
    @ApiOperation(value = "Gets all operations supported by the store",
            notes = "This endpoint returns a list operation names supported by the store.",
            produces = APPLICATION_JSON,
            response = String.class,
            responseContainer = "list",
            responseHeaders = {
                    @ResponseHeader(name = MAESTRO_MEDIA_TYPE_HEADER, description = MAESTRO_MEDIA_TYPE_HEADER_DESCRIPTION)
            })
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK)})
    Response getOperations();

    @GET
    @Path("/details")
    @ApiOperation(value = "Returns a List containing OperationDetails for all supported operations on the executor",
            produces = APPLICATION_JSON,
            response = Object.class,
            responseContainer = "list",
            responseHeaders = {
                    @ResponseHeader(name = MAESTRO_MEDIA_TYPE_HEADER, description = MAESTRO_MEDIA_TYPE_HEADER_DESCRIPTION)
            })
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK)})
    Response getOperationDetails();

    @POST
    @Path("/execute")
    @Produces({APPLICATION_JSON, TEXT_PLAIN})
    @ApiOperation(value = "Performs the given operation on the executor",
            notes = "Attempts to execute the provided operation on the executor, and returns the result below. " +
                    "Simple examples for each operation can be added using the drop-down below.",
            produces = (APPLICATION_JSON + "," + TEXT_PLAIN),
            response = Object.class,
            responseHeaders = {
                    @ResponseHeader(name = JOB_ID_HEADER, description = JOB_ID_HEADER_DESCRIPTION),
                    @ResponseHeader(name = MAESTRO_MEDIA_TYPE_HEADER, description = MAESTRO_MEDIA_TYPE_HEADER_DESCRIPTION)
            })
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK, response = Object.class),
            @ApiResponse(code = 400, message = BAD_REQUEST),
            @ApiResponse(code = 403, message = FORBIDDEN),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR),
            @ApiResponse(code = 501, message = OPERATION_NOT_IMPLEMENTED)})
    Response execute(@ApiParam(value = "The operation to be performed on the executor") final Operation operation);

    @GET
    @Path("/{operationType}")
    @ApiOperation(value = "Gets details about the specified operation",
            notes = "This endpoint exposes the fields (and whether or not they are required); " +
                    "a list of all possible Operations that could follow it; " +
                    "and a small example in JSON, which includes the queried Operation class.",
            produces = APPLICATION_JSON,
            responseHeaders = {
                    @ResponseHeader(name = MAESTRO_MEDIA_TYPE_HEADER, description = MAESTRO_MEDIA_TYPE_HEADER_DESCRIPTION)
            })
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK),
            @ApiResponse(code = 403, message = FORBIDDEN),
            @ApiResponse(code = 404, message = OPERATION_NOT_FOUND),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)})
    Response operationDetails(@ApiParam(value = "The operation type name, for which details should be returned") @PathParam("operationType") final String operationType) throws InstantiationException, IllegalAccessException;

    @GET
    @Path("/{operationType}/example")
    @ApiOperation(value = "Gets example JSON for the specified operation",
            notes = "Returns a fully justified and formatted JSON example of the given Operation, " +
                    "containing a few Operations for demonstration and usage purposes.",
            produces = APPLICATION_JSON,
            responseHeaders = {
                    @ResponseHeader(name = MAESTRO_MEDIA_TYPE_HEADER, description = MAESTRO_MEDIA_TYPE_HEADER_DESCRIPTION)
            })
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK),
            @ApiResponse(code = 403, message = FORBIDDEN),
            @ApiResponse(code = 404, message = OPERATION_NOT_FOUND),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)})
    Response operationExample(@ApiParam(value = "The operation type name, for which a formatted JSON example should be returned") @PathParam("operationType") final String operationType) throws InstantiationException, IllegalAccessException;


}

