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

package uk.gov.gchq.maestro.federatedexecutor.operation.handler.impl;

import com.google.common.collect.Lists;
import org.junit.Before;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.federatedexecutor.operation.AddExecutor;
import uk.gov.gchq.maestro.federatedexecutor.operation.GetAllExecutorIds;
import uk.gov.gchq.maestro.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.user.User;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GetAllExecutorIdsHandlerBasicTest extends MaestroHandlerBasicTest<GetAllExecutorIds, GetAllExecutorIdsHandler> {

    private User testUser1;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        testExecutor.getConfig().addOperationHandler(GetAllExecutorIds.class, new GetAllExecutorIdsHandler());
        testUser1 = new User("testUser1");
        context = new Context(testUser1);
        try {
            final AddExecutorHandlerBasicTest addExecutorHandlerBasicTest = new AddExecutorHandlerBasicTest();
            final AddExecutor basicOp = new AddExecutor().executor(AddExecutorHandlerBasicTest.getInnerExecutor("A"));
            final AddExecutorHandler basicHandler = addExecutorHandlerBasicTest.getBasicHandler();
            basicHandler.doOperation(basicOp, this.context, this.testExecutor);
            final AddExecutor basicOp2 = new AddExecutor().executor(AddExecutorHandlerBasicTest.getInnerExecutor("B"));
            basicHandler.doOperation(basicOp2, this.context, this.testExecutor);
        } catch (Exception e) {
            throw new RuntimeException("Failed setUp", e);
        }
    }

    @Override
    protected GetAllExecutorIdsHandler getBasicHandler() throws Exception {
        return new GetAllExecutorIdsHandler();
    }

    @Override
    protected GetAllExecutorIds getBasicOp() throws Exception {
        return new GetAllExecutorIds();
    }

    @Override
    protected void inspectReturnFromHandler(final Object value) throws Exception {
        inspect(value);
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
        assertTrue(list.contains(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A"));
        assertTrue(list.contains(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "B"));
    }

    @Override
    protected void inspectFields() throws Exception {
        //empty
    }
}
