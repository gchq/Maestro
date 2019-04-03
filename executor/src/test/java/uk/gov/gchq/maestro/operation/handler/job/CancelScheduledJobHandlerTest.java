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
import uk.gov.gchq.maestro.ExecutorProperties;
import uk.gov.gchq.maestro.commonutil.cache.CacheServiceLoader;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclaration;
import uk.gov.gchq.maestro.operation.impl.job.CancelScheduledJob;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class CancelScheduledJobHandlerTest {
    private final ExecutorProperties properties = new ExecutorProperties();
    private final User user = mock(User.class);
    private final CancelScheduledJobHandler handler = new CancelScheduledJobHandler();

    @Before
    public void setup() {
        properties.setJobTrackerEnabled(true);
        properties.set("maestro.cache.service.class", "uk.gov.gchq.maestro.commonutil.cache.impl.HashMapCacheService");
    }

    @Test
    public void shouldThrowExceptionWithNoJobId() {
        // Given
        CancelScheduledJob operation = new CancelScheduledJob.Builder()
                .jobId(null)
                .build();
        final Config config = new Config.Builder()
                .executorProperties(properties)
                .operationHandler(new OperationDeclaration.Builder()
                        .operation(CancelScheduledJob.class)
                        .handler(new CancelScheduledJobHandler())
                        .build())
                .build();

        final Executor executor = new Executor(config);

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
        CacheServiceLoader.shutdown();

        // When / Then
        try {
            handler.doOperation(operation, new Context(user), executor);
            fail("Exception expected");
        } catch (final OperationException e) {
            assertEquals("JobTracker not enabled", e.getMessage());
        }
    }
}
