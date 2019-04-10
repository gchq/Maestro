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

package uk.gov.gchq.maestro.hook;

import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.export.GetExport;
import uk.gov.gchq.maestro.operation.impl.job.GetAllJobDetails;
import uk.gov.gchq.maestro.operation.impl.job.GetJobDetails;
import uk.gov.gchq.maestro.operation.impl.output.ToArray;
import uk.gov.gchq.maestro.operation.impl.output.ToCsv;
import uk.gov.gchq.maestro.operation.impl.output.ToSet;
import uk.gov.gchq.maestro.operation.impl.output.ToSingletonList;
import uk.gov.gchq.maestro.operation.impl.output.ToStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class AdditionalOperationsTest extends HookTest<AdditionalOperations> {
    private static final String ADDITIONAL_OPERATIONS_RESOURCE_PATH = "/additionalOperations.json";

    public AdditionalOperationsTest() {
        super(AdditionalOperations.class);
    }

    @Test
    public void shouldReturnClonedOperations() throws IOException {
        //Given
        final AdditionalOperations additionalOperations = fromJson(ADDITIONAL_OPERATIONS_RESOURCE_PATH);

        // When / Then
        assertClonedOperations(additionalOperations.getStart(), additionalOperations.getStart());
        assertClonedOperations(additionalOperations.getBefore(), additionalOperations.getBefore());
        assertClonedOperations(additionalOperations.getAfter(), additionalOperations.getAfter());
        assertClonedOperations(additionalOperations.getEnd(), additionalOperations.getEnd());
    }

    @Test
    public void shouldSerialiseAndDeserialise() throws IOException {
        // When
        final AdditionalOperations original = new AdditionalOperations();
        original.setStart(Arrays.asList(new ToSet<>(), new ToArray<>()));
        original.setEnd(Arrays.asList(new ToSingletonList<>(), new ToCsv<>()));

        final Map<String, List<Operation>> after = new HashMap<>();
        after.put(ToSet.class.getName(), Arrays.asList(new ToStream<>()));
        after.put(GetExport.class.getName(), Arrays.asList(new GetJobDetails()));
        original.setAfter(after);

        final Map<String, List<Operation>> before = new HashMap<>();
        before.put(ToSet.class.getName(), Arrays.asList(new ToArray<>()));
        before.put(GetJobDetails.class.getName(), Arrays.asList(new GetAllJobDetails()));
        original.setBefore(before);

        final byte[] json = JSONSerialiser.serialise(original);
        final AdditionalOperations cloned = JSONSerialiser.deserialise(json, AdditionalOperations.class);

        // Then
        assertClonedOperations(original.getStart(), cloned.getStart());
        assertClonedOperations(original.getBefore(), cloned.getBefore());
        assertClonedOperations(original.getAfter(), cloned.getAfter());
        assertClonedOperations(original.getEnd(), cloned.getEnd());
    }

    public void assertClonedOperations(final Map<String, List<Operation>> after1, final Map<String, List<Operation>> after2) {
        for (final Map.Entry<String, List<Operation>> entry1 : after1.entrySet()) {
            final List<Operation> ops1 = entry1.getValue();
            final List<Operation> ops2 = after2.get(entry1.getKey());
            assertClonedOperations(ops1, ops2);
        }
    }

    public void assertClonedOperations(final List<Operation> ops1, final List<Operation> ops2) {
        assertEquals(ops1.size(), ops2.size());
        for (int i = 0; i < ops1.size(); i++) {
            assertEquals(ops1.get(i).getClass(), ops2.get(i).getClass());
            assertNotSame(ops1.get(i), ops2.get(i));
        }
    }

    @Override
    protected AdditionalOperations getTestObject() {
        return new AdditionalOperations();
    }

}
