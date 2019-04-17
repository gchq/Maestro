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
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.federatedexecutor.operation.AddExecutor;
import uk.gov.gchq.maestro.federatedexecutor.operation.FederatedExecutorStorage;
import uk.gov.gchq.maestro.federatedexecutor.operation.RemoveExecutor;
import uk.gov.gchq.maestro.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.FederatedHandlersUtil;
import uk.gov.gchq.maestro.util.FederatedPropertiesUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RemoveExecutorHandlerBasicTest extends MaestroHandlerBasicTest<RemoveExecutor, RemoveExecutorHandler> {
    private User testUser1;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        testExecutor.getConfig().addOperationHandler(RemoveExecutor.class, new RemoveExecutorHandler());
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
    protected RemoveExecutorHandler getBasicHandler() throws Exception {
        return new RemoveExecutorHandler();
    }

    @Override
    protected RemoveExecutor getBasicOp() {
        return new RemoveExecutor().graphId(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "B");
    }

    @Override
    protected void inspectFields() throws Exception {
        final ArrayList<String> executorIds = Lists.newArrayList(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A", AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "B");
        try {
            final List<Executor> getAandB = FederatedHandlersUtil.getExecutorsFrom(this.testExecutor, testUser1, executorIds); //TODO is the use of these Utils actually testing anything or sharing failure/bugs?
            fail("exception expected");
        } catch (MaestroCheckedException e) {
            assertTrue(e.getMessage().contains(String.format(FederatedExecutorStorage.ERROR_GETTING_S_FROM_FEDERATED_EXECUTOR_STORAGE_S, executorIds.toString(), "")));
        }
        final List<Executor> getA = FederatedHandlersUtil.getExecutorsFrom(this.testExecutor, testUser1, Lists.newArrayList(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A")); //TODO is the use of these Utils actually testing anything or sharing failure/bugs?
        assertEquals(1, getA.size());
        assertEquals(AddExecutorHandlerBasicTest.getInnerExecutor("A"), getA.get(0));
    }

    @Override
    protected void inspectReturnFromHandler(final Object value) {
        assertNull(value);
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) {
        assertNull(value);
    }
}
