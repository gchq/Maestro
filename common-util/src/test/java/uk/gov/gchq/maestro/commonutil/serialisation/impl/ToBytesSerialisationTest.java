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

package uk.gov.gchq.maestro.commonutil.serialisation.impl;

import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.pair.Pair;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

public abstract class ToBytesSerialisationTest<T> extends SerialisationTest<T, byte[]> {

    @Override
    public void shouldSerialiseNull() throws SerialisationException {
        // When
        final byte[] bytes = serialiser.serialiseNull();

        // Then
        assertArrayEquals(new byte[0], bytes);
    }

    @Test
    @Override
    public void shouldDeserialiseEmpty() throws SerialisationException {
        assertNull(serialiser.deserialiseEmpty());
    }

    @Override
    protected void serialiseFirst(final Pair<T, byte[]> pair) throws SerialisationException {
        byte[] serialise = serialiser.serialise(pair.getFirst());
        assertArrayEquals(Arrays.toString(serialise), pair.getSecond(), serialise);
    }
}
