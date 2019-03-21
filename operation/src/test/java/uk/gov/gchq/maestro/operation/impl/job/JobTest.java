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

package uk.gov.gchq.maestro.operation.impl.job;

import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.jobtracker.Repeat;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class JobTest extends OperationTest {
    final String testJobId = "testId";
    final Operation inputOp = new CancelScheduledJob.Builder()
            .jobId(testJobId)
            .build();
    final Repeat repeat = new Repeat();

    @Override
    public void shouldJsonSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final Job operation = new Job.Builder()
                .operation(inputOp)
                .repeat(repeat)
                .build();

        // When
        byte[] json = JSONSerialiser.serialise(operation, true);
        final Job deserialisedOp = JSONSerialiser.deserialise(json,
                Job.class);

        // Then
        assertEquals(((CancelScheduledJob) inputOp).getJobId(), ((CancelScheduledJob) deserialisedOp.getOpAsOperation()).getJobId());
        assertEquals(repeat, deserialisedOp.getRepeat());
    }

    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final Job op = new Job.Builder()
                .operation(inputOp)
                .repeat(repeat)
                .build();

        // Then
        assertEquals(testJobId, ((CancelScheduledJob) op.getOpAsOperation()).getJobId());
        assertEquals(repeat, op.getRepeat());
    }

    @Override
    public void shouldShallowCloneOperation() {
        // Given
        final Job jobOp = new Job.Builder()
                .operation(inputOp)
                .repeat(repeat)
                .build();

        // When
        Job clone = jobOp.shallowClone();

        // Then
        assertNotSame(jobOp, clone);
        assertNotNull(clone);
        assertEquals(clone.getOpAsOperation(), jobOp.getOpAsOperation());
        assertEquals(clone.getRepeat(), jobOp.getRepeat());
    }

    @Override
    protected Object getTestObject() {
        return new Job();
    }
}
