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

import static uk.gov.gchq.maestro.commonutil.exception.Status.INTERNAL_SERVER_ERROR;

/**
 * Subtype of {@link Exception} with additional constructors to support the inclusion
 * of a HTTP error message along with the other exception details.
 */
public class MaestroCheckedException extends Exception {

    private Status status = INTERNAL_SERVER_ERROR;

    public MaestroCheckedException(final Throwable cause, final Status status) {
        super(cause);
        this.status = status;
    }

    public MaestroCheckedException(final String message, final Status status) {
        super(message);
        this.status = status;
    }

    public MaestroCheckedException(final String message, final Throwable cause, final Status status) {
        super(message, cause);
        this.status = status;
    }

    public MaestroCheckedException(final Throwable cause) {
        super(cause);
    }

    public MaestroCheckedException(final String message) {
        super(message);
    }

    public MaestroCheckedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }
}
