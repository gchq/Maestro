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

package uk.gov.gchq.maestro.operation.handler.named;

import com.google.common.collect.Iterables;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.cache.CacheServiceLoader;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.named.operation.AddNamedOperation;
import uk.gov.gchq.maestro.named.operation.GetAllNamedOperations;
import uk.gov.gchq.maestro.named.operation.NamedOperationDetail;
import uk.gov.gchq.maestro.operation.handler.named.cache.NamedOperationCache;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Config;

import java.util.ArrayList;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.gchq.maestro.commonutil.cache.util.CacheProperties.CACHE_SERVICE_CLASS;

public class GetAllNamedOperationsHandlerTest {
    private final NamedOperationCache cache = new NamedOperationCache();
    private final AddNamedOperationHandler addNamedOperationHandler = new AddNamedOperationHandler(cache);
    private final GetAllNamedOperationsHandler getAllNamedOperationsHandler = new GetAllNamedOperationsHandler(cache);
    private Context context = new Context(new User.Builder()
            .userId(User.UNKNOWN_USER_ID)
            .build());

    private final NamedOperationDetail expectedOperationDetailWithInputType = new NamedOperationDetail.Builder()
            .operationName("exampleOp")
            .inputType("java.lang.Object")
            .creatorId(User.UNKNOWN_USER_ID)
            .operationChain("{\"operations\":[{\"class\":\"uk.gov.gchq.maestro.operation.impl.output.ToSingletonList\"}]}")
            .readers(new ArrayList<>())
            .writers(new ArrayList<>())
            .build();

    private final NamedOperationDetail expectedOperationDetailWithoutInputType = new NamedOperationDetail.Builder()
            .operationName("exampleOp")
            .inputType(null)
            .creatorId(User.UNKNOWN_USER_ID)
            .operationChain("{\"operations\":[{\"class\":\"uk.gov.gchq.maestro.operation.impl.job.GetAllJobDetails\"}]}")
            .readers(new ArrayList<>())
            .writers(new ArrayList<>())
            .build();

    private Config config;
    private Executor executor;

    @AfterClass
    public static void tearDown() {
        CacheServiceLoader.shutdown();
    }

    @Before
    public void before() {
        Properties properties = new Properties();
        properties.put(CACHE_SERVICE_CLASS, "uk.gov.gchq.maestro.commonutil.cache.impl.HashMapCacheService");
        config = new Config.Builder().executorProperties(properties).build();
        executor = new Executor(config);
    }

    @Test
    public void shouldReturnNamedOperationWithInputType() throws Exception {
        // Given
        AddNamedOperation addNamedOperation = new AddNamedOperation.Builder()
                .name(expectedOperationDetailWithInputType.getOperationName())
                .description(expectedOperationDetailWithInputType.getDescription())
                .operationChain(expectedOperationDetailWithInputType.getOperationChainWithDefaultParams())
                .build();

        addNamedOperationHandler.doOperation(addNamedOperation, context, executor);

        // When
        CloseableIterable<NamedOperationDetail> allNamedOperationsList =
                getAllNamedOperationsHandler.doOperation(new GetAllNamedOperations(), context, executor);

        // Then
        assertEquals(1, Iterables.size(allNamedOperationsList));
        assertTrue(Iterables.contains(allNamedOperationsList, expectedOperationDetailWithInputType));
    }

    @Test
    public void shouldReturnNamedOperationWithNoInputType() throws Exception {
        // Given
        AddNamedOperation addNamedOperation = new AddNamedOperation.Builder()
                .name(expectedOperationDetailWithoutInputType.getOperationName())
                .description(expectedOperationDetailWithoutInputType.getDescription())
                .operationChain(expectedOperationDetailWithoutInputType.getOperationChainWithDefaultParams())
                .build();

        addNamedOperationHandler.doOperation(addNamedOperation, context, executor);

        // When
        CloseableIterable<NamedOperationDetail> allNamedOperationsList =
                getAllNamedOperationsHandler.doOperation(new GetAllNamedOperations(), context, executor);

        // Then
        assertEquals(1, Iterables.size(allNamedOperationsList));
        assertTrue(Iterables.contains(allNamedOperationsList, expectedOperationDetailWithoutInputType));
    }
}
