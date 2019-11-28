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
 * A {@code LimitExceededException} is a {@link RuntimeException} that will be
 * thrown when an operation exceeds, or would exceed, some constraining limit.
 */
public class LimitExceededException extends RuntimeException {

    private static final long serialVersionUID = -2781889679032919265L;

    public LimitExceededException(final String message) {
        super(message);
    }

}
