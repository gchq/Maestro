/*
 * Copyright 2016-2019 Crown Copyright
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

import uk.gov.gchq.maestro.operation.Operation;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * An {@code IOperationService} has methods to execute {@link Operation}s on the
 * {@link uk.gov.gchq.maestro.Executor}.
 */
@Path("/graph/doOperation")
@Api(value = "operations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public interface IOperationService {
    @POST
    @Path("/operation")
    @ApiOperation(value = "Performs the given operation on the graph", response = Object.class)
    Object execute(final Operation operation);
}
