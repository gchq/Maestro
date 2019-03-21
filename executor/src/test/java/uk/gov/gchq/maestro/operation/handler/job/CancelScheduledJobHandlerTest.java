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

package uk.gov.gchq.maestro.operation.handler.job;

import org.junit.Test;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.jobtracker.JobTracker;
import uk.gov.gchq.maestro.operation.impl.job.CancelScheduledJob;
import uk.gov.gchq.maestro.user.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class CancelScheduledJobHandlerTest {

    @Test
    public void shouldThrowExceptionWithNoJobId() {
        // Given
        CancelScheduledJob operation = new CancelScheduledJob.Builder()
                .jobId(null)
                .build();
        CancelScheduledJobHandler handler = new CancelScheduledJobHandler();
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);

        given(JobTracker.isCacheEnabled()).willReturn(true);

        // When / Then
        try {
            handler.doOperation(operation, new Context(user), executor);
            fail("Exception expected");
        } catch (final OperationException e) {
            assertTrue(e.getMessage().contains("job id must be specified"));
        }
    }

    @Test
    public void shouldThrowExceptionIfJobTrackerIsNotConfigured() {
        // Given
        final CancelScheduledJobHandler handler = new CancelScheduledJobHandler();
        final CancelScheduledJob operation = mock(CancelScheduledJob.class);
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);

        given(JobTracker.isCacheEnabled()).willReturn(false);

        // When / Then
        try {
            handler.doOperation(operation, new Context(user), executor);
            fail("Exception expected");
        } catch (final OperationException e) {
            assertEquals("JobTracker not enabled", e.getMessage());
        }
    }
}