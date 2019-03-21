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

package uk.gov.gchq.maestro.operation.impl.export;

import org.junit.Test;

import uk.gov.gchq.maestro.exception.SerialisationException;
import uk.gov.gchq.maestro.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.OperationTest;
import uk.gov.gchq.maestro.operation.impl.export.set.GetSetExport;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;


public class GetExportsTest extends OperationTest<GetExports> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final GetExports op = new GetExports.Builder()
                .exports(new GetSetExport.Builder()
                                .key("key1")
                                .build(),
                        new GetSetExport.Builder()
                                .key("key2")
                                .build())
                .build();

        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final GetExports deserialisedOp = JSONSerialiser.deserialise(json, GetExports.class);

        // Then
        assertEquals("key1", deserialisedOp.getGetExports().get(0).getKey());
        assertEquals("key2", deserialisedOp.getGetExports().get(1).getKey());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final GetExports op = new GetExports.Builder()
                .exports(new GetSetExport.Builder()
                                .key("key1")
                                .build(),
                        new GetSetExport.Builder()
                                .key("key2")
                                .build())
                .build();

        // Then
        assertEquals("key1", op.getGetExports().get(0).getKey());
        assertEquals("key2", op.getGetExports().get(1).getKey());
    }

    @Override
    public void shouldShallowCloneOperation() {
        // Given
        final GetSetExport getSetExport = new GetSetExport.Builder()
                .key("key1")
                .build();

        final GetExports getExports = new GetExports.Builder()
                .exports(getSetExport)
                .build();

        // When
        final GetExports clone = getExports.shallowClone();

        // Then
        assertNotSame(getExports, clone);
        assertEquals(getSetExport, clone.getGetExports().iterator().next());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();

        // Then
        assertEquals(Map.class, outputClass);
    }

    @Override
    protected GetExports getTestObject() {
        return new GetExports();
    }
}
