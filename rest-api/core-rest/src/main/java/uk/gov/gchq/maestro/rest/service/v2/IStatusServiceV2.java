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

import uk.gov.gchq.maestro.rest.SystemStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static uk.gov.gchq.maestro.rest.ServiceConstants.INTERNAL_SERVER_ERROR;
import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE_HEADER;
import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE_HEADER_DESCRIPTION;
import static uk.gov.gchq.maestro.rest.ServiceConstants.OK;

/**
 * An {@code StatusService} has methods to check the status of the system
 */
@Path("/executor/status")
@Produces(APPLICATION_JSON)
@Api(value = "/status")
public interface IStatusServiceV2 {

    @GET
    @ApiOperation(value = "Returns the status of the service",
            notes = "A simple way to check the current status of the application/service.",
            response = SystemStatus.class,
            produces = APPLICATION_JSON,
            responseHeaders = {
                    @ResponseHeader(name = MAESTRO_MEDIA_TYPE_HEADER, description = MAESTRO_MEDIA_TYPE_HEADER_DESCRIPTION)
            })
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR),
            @ApiResponse(code = 503, message = "The service is not available")})
    Response status();
}
