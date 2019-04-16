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

package uk.gov.gchq.maestro.operation.impl.output;

import com.google.common.collect.Sets;
import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.data.generator.StringGenerator;
import uk.gov.gchq.maestro.operation.OperationTest;

import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

public class ToCsvTest extends OperationTest<ToCsv> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final ToCsv op = new ToCsv.Builder<>().build();

        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final ToCsv deserialisedOp = JSONSerialiser.deserialise(json, ToCsv.class);

        // Then
        assertNotNull(deserialisedOp);
    }

    @Override
    public void builderShouldCreatePopulatedOperation() {
        // Given
        final Integer input = 4;
        final StringGenerator generator = new StringGeneratorImpl();
        final ToCsv toCsv = new ToCsv.Builder<>()
                .generator(generator)
                .input(input)
                .build();

        // Then
        assertThat(toCsv.getInput(), is(notNullValue()));
        assertEquals(generator, toCsv.getElementGenerator());
    }

    @Override
    public void shouldShallowCloneOperation() {
        // Given
        final Integer input = 4;
        final StringGenerator generator = new StringGeneratorImpl();
        final ToCsv toCsv = new ToCsv.Builder<Integer>()
                .generator(generator)
                .input(input)
                .build();

        // When
        final ToCsv clone = toCsv.shallowClone();

        // Then
        assertNotSame(toCsv, clone);
        assertEquals(input, clone.getInput().iterator().next());
        assertEquals(generator, clone.getElementGenerator());
    }

    @Override
    public Set<String> getRequiredFields() {
        return Sets.newHashSet("stringGenerator");
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();

        // Then
        assertEquals(Iterable.class, outputClass);
    }

    @Override
    protected ToCsv getTestObject() {
        return new ToCsv();
    }

    private class StringGeneratorImpl extends StringGenerator<Integer> {

        @Override
        protected String _apply(final Integer object) {
            return object.toString();
        }
    }
}
