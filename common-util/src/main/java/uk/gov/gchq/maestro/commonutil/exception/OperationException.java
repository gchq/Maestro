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
 * An {@code OperationException} is thrown when an operation fails.
 */
public class OperationException extends MaestroCheckedException {

    private static final long serialVersionUID = -7470208543681985108L;

    public OperationException(final Throwable cause) {
        super(cause);
    }

    public OperationException(final String message) {
        super(message, INTERNAL_SERVER_ERROR);
    }

    public OperationException(final String message, final Status status) {
        super(message, status);
    }

    public OperationException(final String message, final Throwable e) {
        super(message, e, INTERNAL_SERVER_ERROR);
    }

    public OperationException(final String message, final Throwable e, final Status status) {
        super(message, e, status);
    }
}
