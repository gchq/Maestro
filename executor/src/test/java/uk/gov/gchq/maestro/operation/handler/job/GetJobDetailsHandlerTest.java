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

import org.junit.Before;
import org.junit.Test;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.cache.CacheServiceLoader;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.jobtracker.JobDetail;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclaration;
import uk.gov.gchq.maestro.operation.impl.job.GetJobDetails;
import uk.gov.gchq.maestro.operation.impl.job.Job;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Config;
import uk.gov.gchq.maestro.util.ExecutorPropertiesUtil;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class GetJobDetailsHandlerTest {
    private final Properties properties = new Properties();
    private final User user = mock(User.class);
    private final Operation op = mock(Operation.class);
    private final GetJobDetailsHandler handler = new GetJobDetailsHandler();

    @Before
    public void setup() {
        ExecutorPropertiesUtil.setJobTrackerEnabled(properties, true);
        ExecutorPropertiesUtil.setCacheClass(properties, "uk.gov.gchq.maestro.commonutil.cache.impl.HashMapCacheService");
    }

    @Test
    public void shouldThrowExceptionIfJobTrackerIsNotConfigured() {
        // Given
        final GetJobDetails operation = new GetJobDetails();
        final Executor executor = mock(Executor.class);
        CacheServiceLoader.shutdown();

        // When / Then
        try {
            handler.doOperation(operation, new Context(user), executor);
            fail("Exception expected");
        } catch (final OperationException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldGetJobDetailsByDelegatingToJobTracker() throws OperationException {
        // Given
        final Config config = new Config.Builder()
                .executorProperties(properties)
                .operationHandler(new OperationDeclaration.Builder()
                        .operation(Job.class)
                        .handler(new JobHandler())
                        .build())
                .build();

        final Executor executor = new Executor(config);

        final Context context = new Context(user);

        JobDetail addedJobDetail = executor.execute(new Job.Builder()
                .operation(op)
                .build(), user);

        final GetJobDetails operation = new GetJobDetails.Builder()
                .jobId(addedJobDetail.getJobId())
                .build();

        // When
        final JobDetail result = handler.doOperation(operation, context, executor);

        // Then
        assertEquals(addedJobDetail.getJobId(), result.getJobId());
        assertEquals(addedJobDetail.getOpAsOperation(), result.getOpAsOperation());
    }
}
