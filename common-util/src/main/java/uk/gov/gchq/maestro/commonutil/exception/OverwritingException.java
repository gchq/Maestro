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

/**
 * An {@code OverwritingException} should be thrown when a condition will cause
 * something to be overwritten.
 */
public class OverwritingException extends IllegalArgumentException {
    private static final long serialVersionUID = -4189349368741071943L;

    /**
     * Constructs a new overwriting exception with null as its detail message.
     */
    public OverwritingException() {
    }

    /**
     * Constructs a new overwriting exception with the specified detail message.
     *
     * @param message Overwriting exception detail message.
     */
    public OverwritingException(final String message) {
        super(message);
    }

    /**
     * Constructs a new overwriting exception with the specified detail message and cause.
     *
     * @param message Overwriting exception detail message.
     * @param cause   Overwriting exception cause.
     */
    public OverwritingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
