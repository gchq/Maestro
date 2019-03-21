/*
 * Copyright 2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
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
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.jobtracker.JobDetail;
import uk.gov.gchq.maestro.jobtracker.JobTracker;
import uk.gov.gchq.maestro.operation.impl.job.GetAllJobDetails;
import uk.gov.gchq.maestro.user.User;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class GetAllJobDetailsHandlerTest {

    @Test
    public void shouldThrowExceptionIfJobTrackerIsNotConfigured() {
        // Given
        final GetAllJobDetailsHandler handler = new GetAllJobDetailsHandler();
        final GetAllJobDetails operation = mock(GetAllJobDetails.class);
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);

        given(JobTracker.isCacheEnabled()).willReturn(true);

        // When / Then
        try {
            handler.doOperation(operation, new Context(user), executor);
            fail("Exception expected");
        } catch (final OperationException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldGetAllJobDetailsByDelegatingToJobTracker() throws OperationException {
        // Given
        final GetAllJobDetailsHandler handler = new GetAllJobDetailsHandler();
        final GetAllJobDetails operation = mock(GetAllJobDetails.class);
        final Executor executor = mock(Executor.class);
        final JobTracker jobTracker = mock(JobTracker.class);
        final User user = mock(User.class);
        final CloseableIterable<JobDetail> jobsDetails = mock(CloseableIterable.class);

        given(jobTracker.isCacheEnabled()).willReturn(true);
        given(jobTracker.getAllJobs(user)).willReturn(jobsDetails);

        // When
        final CloseableIterable<JobDetail> results = handler.doOperation(operation, new Context(user), executor);

        // Then
        assertSame(jobsDetails, results);
    }
}
