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

package uk.gov.gchq.maestro.data.generator;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.maestro.commonutil.iterable.TransformIterable;

/**
 * Generates a string for each object.
 */
@Since("1.0.0")
@Summary("Generates a string for each object")
public abstract class StringGenerator<INPUT> implements ObjectGenerator<INPUT, String> {

    public Iterable<String> apply(final Iterable<? extends INPUT> inputObjs) {
        return new TransformIterable<INPUT, String>(inputObjs) {
            @Override
            protected String transform(final INPUT input) {
                return _apply(input);
            }
        };
    }

    /**
     * @param object the object to convert
     * @return the generated string
     */
    protected abstract String _apply(final INPUT object);
}
