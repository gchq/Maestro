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

package uk.gov.gchq.maestro.operation.impl.export.resultcache;

import com.google.common.collect.Sets;
import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.OperationTest;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;


public class ExportToResultCacheTest extends OperationTest<ExportToResultCache> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final String key = "key";
        final HashSet<String> opAuths = Sets.newHashSet("1", "2");
        final ExportToResultCache op = new ExportToResultCache.Builder<>()
                .opAuths(opAuths)
                .key(key)
                .build();

        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final ExportToResultCache deserialisedOp = JSONSerialiser.deserialise(json, ExportToResultCache.class);

        // Then
        assertEquals(key, deserialisedOp.getKey());
        assertEquals(opAuths, deserialisedOp.getOpAuths());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final String key = "key";
        final HashSet<String> opAuths = Sets.newHashSet("1", "2");
        final ExportToResultCache op = new ExportToResultCache.Builder<>()
                .opAuths(opAuths)
                .key(key)
                .build();

        // Then
        assertEquals(key, op.getKey());
        assertEquals(opAuths, op.getOpAuths());
    }

    @Override
    public void shouldShallowCloneOperation() {
        // Given
        final String key = "key";
        final HashSet<String> opAuths = Sets.newHashSet("1", "2");
        final String input = "input";
        final ExportToResultCache exportToGafferResultCache = new ExportToResultCache.Builder<>()
                .key(key)
                .opAuths(opAuths)
                .input(input)
                .build();

        // When
        ExportToResultCache clone = exportToGafferResultCache.shallowClone();

        // Then
        assertNotSame(exportToGafferResultCache, clone);
        assertEquals(key, clone.getKey());
        assertEquals(input, clone.getInput());
        assertEquals(opAuths, clone.getOpAuths());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();

        // Then
        assertEquals(Object.class, outputClass);
    }

    @Override
    protected ExportToResultCache getTestObject() {
        return new ExportToResultCache();
    }
}

