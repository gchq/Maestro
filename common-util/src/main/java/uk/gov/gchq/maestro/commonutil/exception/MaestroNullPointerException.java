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

public class MaestroNullPointerException extends MaestroRuntimeException {
    private static final long serialVersionUID = -1653881390914784442L;

    public MaestroNullPointerException(final String message) {
        super(message);
    }

    public MaestroNullPointerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MaestroNullPointerException(final String message, final Status status) {
        super(message, status);
    }

    public MaestroNullPointerException(final String message, final Throwable cause, final Status status) {
        super(message, cause, status);
    }
}
