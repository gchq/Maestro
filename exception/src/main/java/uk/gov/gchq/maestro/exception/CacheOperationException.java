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

package uk.gov.gchq.maestro.exception;

/**
 * Exception indicating than an error occured while using the cache service.
 */
public class CacheOperationException extends MaestroCheckedException {

    public CacheOperationException(final String message) {
        super(message);
    }

    public CacheOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CacheOperationException(final Throwable cause) {
        super(cause);
    }

    public CacheOperationException(final Throwable cause, final Status status) {
        super(cause, status);
    }

    public CacheOperationException(final String message, final Status status) {
        super(message, status);
    }

    public CacheOperationException(final String message, final Throwable cause, final Status status) {
        super(message, cause, status);
    }
}
