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

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Empty interface containing the Swagger API definitions for the v2 Maestro REST
 * API.
 */
@SwaggerDefinition(
        info = @Info(
                version = "v2",
                title = "",
                description = "A number of configured properties can be retrieved from the \"config\" endpoints. " +
                        "If enabled, the \"job\" endpoints allow the retrieval of job details, as well as submitting a new job. " +
                        "\"Operation\" endpoints include information on available ops, individual ops, and execution of ops. " +
                        "\"Properties\" endpoints expose the application-specific properties. " +
                        "The \"status\" endpoint returns information regarding the current status of the service."
        ),
        consumes = {APPLICATION_JSON},
        produces = {APPLICATION_JSON},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS}
)
public interface SwaggerDefinitionConfigV2 {
    // Empty marker interface
}
