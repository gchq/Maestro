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

package uk.gov.gchq.maestro.rest.mapper;

import uk.gov.gchq.maestro.commonutil.exception.Error;
import uk.gov.gchq.maestro.commonutil.exception.ErrorFactory;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE;
import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE_HEADER;

/**
 * Jersey {@link javax.ws.rs.ext.ExceptionMapper} to be used to handle
 * {@link uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException}s.
 */
@Provider
public class MaestroCheckedExceptionMapper implements ExceptionMapper<MaestroCheckedException> {

    @Override
    public Response toResponse(final MaestroCheckedException mce) {
        final Error error = ErrorFactory.from(mce);

        return Response.status(error.getStatusCode())
                       .header(MAESTRO_MEDIA_TYPE_HEADER, MAESTRO_MEDIA_TYPE)
                       .entity(error)
                       .build();
    }
}
