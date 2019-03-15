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

package uk.gov.gchq.maestro;

import java.io.IOException;

/**
 * An {@code SerialisationException} is thrown when serialisation/deserialisation fails.
 */
public class SerialisationException extends IOException {
    private static final long serialVersionUID = 1624476078972832393L;

    public SerialisationException(final String message) {
        super(message);
    }

    public SerialisationException(final String message, final Throwable e) {
        super(message, e);
    }
}
