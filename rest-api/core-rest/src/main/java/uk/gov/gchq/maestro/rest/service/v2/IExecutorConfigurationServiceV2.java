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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static uk.gov.gchq.maestro.rest.ServiceConstants.INTERNAL_SERVER_ERROR;
import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE_HEADER;
import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE_HEADER_DESCRIPTION;
import static uk.gov.gchq.maestro.rest.ServiceConstants.OK;

/**
 * An {@code IExecutorConfigurationService} has methods to get {@link uk.gov.gchq.maestro.Executor} configuration information
 * such as the {@link uk.gov.gchq.maestro.operation.handler.OperationHandler}'s available.
 */
@Path("/executor/config")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Api(value = "config")
public interface IExecutorConfigurationServiceV2 {

    @GET
    @Path("/description")
    @Produces(TEXT_PLAIN)
    @ApiOperation(value = "Gets the Executor description",
            response = String.class,
            produces = TEXT_PLAIN,
            responseHeaders = {
                    @ResponseHeader(name = MAESTRO_MEDIA_TYPE_HEADER, description = MAESTRO_MEDIA_TYPE_HEADER_DESCRIPTION)
            })
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)})
    Response getDescription();
}
