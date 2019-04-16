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

package uk.gov.gchq.maestro.operation.impl;

import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.OperationTest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

public class ScoreOperationChainTest extends OperationTest<ScoreOperationChain> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final ScoreOperationChain op = new ScoreOperationChain();

        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final ScoreOperationChain deserialisedOp = JSONSerialiser.deserialise(json, ScoreOperationChain.class);

        // Then
        assertNotNull(deserialisedOp);
    }

    @Override
    public void builderShouldCreatePopulatedOperation() {
        // Given
        final OperationChain opChain = new OperationChain();
        final ScoreOperationChain scoreOperationChain = new ScoreOperationChain.Builder()
                .operationChain(opChain)
                .build();

        // Then
        assertThat(scoreOperationChain.getOperationChain(), is(notNullValue()));
    }

    @Override
    public void shouldShallowCloneOperation() {
        // Given
        final OperationChain opChain = new OperationChain();
        final ScoreOperationChain scoreOperationChain = new ScoreOperationChain.Builder()
                .operationChain(opChain)
                .build();

        // When
        ScoreOperationChain clone = scoreOperationChain.shallowClone();

        // Then
        assertNotSame(scoreOperationChain, clone);
        assertEquals(opChain, clone.getOperationChain());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();

        // Then
        assertEquals(Integer.class, outputClass);
    }

    @Override
    protected ScoreOperationChain getTestObject() {
        return new ScoreOperationChain();
    }
}
