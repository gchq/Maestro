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

import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.OperationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;


public class GetResultCacheExportTest extends OperationTest<GetResultCacheExport> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final String key = "key";
        final GetResultCacheExport op = new GetResultCacheExport.Builder()
                .key(key)
                .build();

        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final GetResultCacheExport deserialisedOp = JSONSerialiser.deserialise(json, GetResultCacheExport.class);

        // Then
        assertEquals(key, deserialisedOp.getKey());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final String key = "key";
        final GetResultCacheExport op = new GetResultCacheExport.Builder()
                .key(key)
                .build();

        // Then
        assertEquals(key, op.getKey());
    }

    @Override
    public void shouldShallowCloneOperation() {
        // Given
        final String key = "key";
        final String jobId = "jobId";
        final GetResultCacheExport getGafferResultCacheExport = new GetResultCacheExport.Builder()
                .key(key)
                .jobId(jobId)
                .build();

        // When
        GetResultCacheExport clone = getGafferResultCacheExport.shallowClone();

        // Then
        assertNotSame(getGafferResultCacheExport, clone);
        assertEquals(key, clone.getKey());
        assertEquals(jobId, clone.getJobId());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();

        // Then
        assertEquals(CloseableIterable.class, outputClass);
    }

    @Override
    protected GetResultCacheExport getTestObject() {
        return new GetResultCacheExport();
    }
}
