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

import org.junit.Test;

import uk.gov.gchq.maestro.exception.SerialisationException;
import uk.gov.gchq.maestro.jsonserialisation.JSONSerialiser;


import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

public abstract class MaestroObjectTest<T> {

    @Test
    public void shouldJSONSerialise() throws SerialisationException {
        final T testObject = getTestObject();

        final String executorString = getJSONString();
        requireNonNull(executorString);
        final byte[] serialise = JSONSerialiser.serialise(testObject, true);
        assertEquals(executorString, new String(serialise));
        assertEquals(testObject, JSONSerialiser.deserialise(serialise, getTestObjectClass()));
    }

    protected abstract Class<T> getTestObjectClass();

    protected abstract String getJSONString();

    protected abstract T getTestObject();
}
