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

package uk.gov.gchq.maestro.federated.operation.handler;

import com.google.common.collect.Lists;
import org.junit.Before;

import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.handler.AddExecutorHandler;
import uk.gov.gchq.maestro.federated.handler.GetAllExecutorIdsHandler;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GetAllExecutorIdsHandlerBasicTest extends MaestroHandlerBasicTest<GetAllExecutorIdsHandler> {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        try {
            Objects.requireNonNull(testExecutor);
            final AddExecutorHandler basicHandler = new AddExecutorHandler();
            final Operation addExecutor = new Operation("AddExecutor");

            basicHandler.doOperation(addExecutor.operationArg(AddExecutorHandler.EXECUTOR, new Executor(AddExecutorHandlerBasicTest.getInnerConfig("A"))), this.context, testExecutor);
            basicHandler.doOperation(addExecutor.operationArg(AddExecutorHandler.EXECUTOR, new Executor(AddExecutorHandlerBasicTest.getInnerConfig("B"))), this.context, testExecutor);
        } catch (Exception e) {
            throw new RuntimeException("Failed setUp of getTestExecutor", e);
        }
    }

    @Override
    protected GetAllExecutorIdsHandler getTestHandler() throws Exception {
        return new GetAllExecutorIdsHandler();
    }

    @Override
    protected Operation getBasicOp() throws Exception {
        return new Operation("GetAllExecutorIds");
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
        inspect(value);
    }

    private void inspect(final Object value) {
        assertNotNull(value);
        assertTrue(value instanceof Collection);
        final ArrayList<Object> list = Lists.newArrayList((Iterable<Object>) value);
        assertEquals(2, list.size());
        assertTrue(list.contains(new Executor(new Config(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A"))));
        assertTrue(list.contains(new Executor(new Config(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "B"))));
    }

    @Override
    protected void inspectFields() throws Exception {
        //empty
    }

    @Override
    protected Class<GetAllExecutorIdsHandler> getTestObjectClass() {
        return GetAllExecutorIdsHandler.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.federated.handler.GetAllExecutorIdsHandler\"\n" +
                "}";
    }
}
