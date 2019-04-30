/*
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.maestro.rest.service.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import uk.gov.gchq.gaffer.rest.SystemStatus;
import uk.gov.gchq.maestro.rest.SystemStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static uk.gov.gchq.gaffer.rest.ServiceConstants.INTERNAL_SERVER_ERROR;
import static uk.gov.gchq.gaffer.rest.ServiceConstants.OK;

/**
 * An {@code StatusService} has methods to check the status of the system
 */
@Path("/status")
@Produces(APPLICATION_JSON)
@Api(value = "/status")
public interface IStatusService {

    @GET
    @ApiOperation(value = "Returns the status of the service", response = SystemStatus.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = OK),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)})
    SystemStatus status();
}
