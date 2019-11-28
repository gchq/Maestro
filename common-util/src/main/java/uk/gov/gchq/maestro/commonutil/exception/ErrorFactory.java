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

package uk.gov.gchq.maestro.commonutil.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static utility class to standardise the instantiation of {@link Error}
 * objects.
 */
public final class ErrorFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorFactory.class);

    /**
     * Empty, private constructor to prevent instantiation.
     */
    private ErrorFactory() {
        // Empty
    }

    /**
     * Create an {@link Error} object from an
     * {@link UnauthorisedException}.
     *
     * @param e the exception object
     * @return a newly constructed {@link Error}
     */
    public static Error from(final UnauthorisedException e) {
        LOGGER.error("Error: {}", e.getMessage(), e);
        return new Error.ErrorBuilder()
                .status(Status.FORBIDDEN)
                .simpleMessage(e.getMessage())
                .detailMessage(ExceptionUtils.getStackTrace(e))
                .build();
    }

    /**
     * Create an {@link Error} object from a
     * {@link MaestroCheckedException}.
     *
     * @param gex the exception object
     * @return a newly constructed {@link Error}
     */
    public static Error from(final MaestroCheckedException gex) {
        LOGGER.error("Error: {}", gex.getMessage(), gex);
        return new Error.ErrorBuilder()
                .status(gex.getStatus())
                .simpleMessage(gex.getMessage())
                .detailMessage(ExceptionUtils.getStackTrace(gex))
                .build();
    }

    /**
     * Create an {@link Error} object from a
     * {@link MaestroRuntimeException}.
     *
     * @param gex the exception object
     * @return a newly constructed {@link Error}
     */
    public static Error from(final MaestroRuntimeException gex) {
        LOGGER.error("Error: {}", gex.getMessage(), gex);
        return new Error.ErrorBuilder()
                .status(gex.getStatus())
                .simpleMessage(gex.getMessage())
                .detailMessage(ExceptionUtils.getStackTrace(gex))
                .build();
    }

    /**
     * Create an {@link Error} object from a
     * {@link MaestroWrappedErrorRuntimeException}.
     *
     * @param gex the exception object
     * @return the error from within the exception
     */
    public static Error from(final MaestroWrappedErrorRuntimeException gex) {
        LOGGER.error("Error: {}", gex.getError().getSimpleMessage(), gex);
        return gex.getError();
    }

    /**
     * Create an {@link Error} object from an
     * {@link Exception}.
     *
     * @param ex the exception object
     * @return a newly constructed {@link Error}
     */
    public static Error from(final Exception ex) {
        LOGGER.error("Error: {}", ex.getMessage(), ex);
        return new Error.ErrorBuilder()
                .status(Status.INTERNAL_SERVER_ERROR)
                .simpleMessage(ex.getMessage())
                .detailMessage(ExceptionUtils.getStackTrace(ex))
                .build();
    }
}
