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
package uk.gov.gchq.maestro.rest;

import org.apache.commons.lang3.exception.ExceptionUtils;


import uk.gov.gchq.maestro.commonutil.exception.Error;

import javax.ws.rs.WebApplicationException;

/**
 * Static utility class to standardise the instantiation of {@link Error}
 * objects from {@link javax.ws.rs.WebApplicationException}s.
 */
public final class JaxRsErrorFactory {

    /**
     * Empty, private constructor to prevent instantiation.
     */
    private JaxRsErrorFactory() {
        // Empty
    }

    /**
     * Create an {@link Error} object from a
     * {@link javax.ws.rs.WebApplicationException}.
     *
     * @param ex the exception object
     * @return a newly constructed {@link Error}
     */
    public static Error from(final WebApplicationException ex) {
        return new Error.ErrorBuilder().statusCode(ex.getResponse().getStatus())
                                 .simpleMessage(ex.getMessage())
                                 .detailMessage(ExceptionUtils.getStackTrace(ex))
                                 .build();
    }
}
