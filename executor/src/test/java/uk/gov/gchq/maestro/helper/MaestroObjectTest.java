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

package uk.gov.gchq.maestro.helper;

import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class MaestroObjectTest<T> {

    @Test
    public void shouldJSONSerialise() throws Exception {
        final T testObject = getTestObject();
        requireNonNull(testObject);
        final String jsonString = getJSONString();
        requireNonNull(jsonString);

        final byte[] serialisedTestObject = JSONSerialiser.serialise(testObject, true);
        assertEquals("json strings are not equal",jsonString, new String(serialisedTestObject));
        assertEquals("deserialised object is not the same",testObject, JSONSerialiser.deserialise(serialisedTestObject, getTestObjectClass()));
    }

    protected abstract Class<T> getTestObjectClass();

    protected abstract String getJSONString();

    protected abstract T getTestObject() throws Exception;
}
