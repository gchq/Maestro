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

package uk.gov.gchq.maestro.util.hook;

import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.JSONSerialisationTest;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;

public abstract class GraphHookTest<T> extends JSONSerialisationTest<T> {
    private final Class<T> hookClass;

    protected GraphHookTest(final Class<T> hookClass) {
        this.hookClass = hookClass;
    }

    protected T fromJson(final String path) {
        try {
            return JSONSerialiser.deserialise(StreamUtil.openStream(getClass(), path), hookClass);
        } catch (final SerialisationException e) {
            throw new RuntimeException(e);
        }
    }
}
