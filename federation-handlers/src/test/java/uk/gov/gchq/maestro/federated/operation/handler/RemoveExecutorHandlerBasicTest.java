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

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.federated.operation.AddExecutor;
import uk.gov.gchq.maestro.federated.operation.RemoveExecutor;
import uk.gov.gchq.maestro.federated.util.FederatedHandlersUtil;
import uk.gov.gchq.maestro.helper.MaestroHandlerBasicTest;

import java.util.ArrayList;
import java.util.List;

public class RemoveExecutorHandlerBasicTest extends MaestroHandlerBasicTest<RemoveExecutor, RemoveExecutorHandler> {

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
            final List<Executor> getAandB = FederatedHandlersUtil.getExecutorsFrom(this.testExecutor, testUser, executorIds); //TODO is the use of these Utils actually testing anything or sharing failure/bugs?
            Assert.fail("exception expected");
        } catch (MaestroCheckedException e) {
            Assert.assertTrue(e.getMessage().contains(String.format(FederatedExecutorStorage.ERROR_GETTING_S_FROM_FEDERATED_EXECUTOR_STORAGE_S, executorIds.toString(), "")));
        }
        final List<Executor> getA = FederatedHandlersUtil.getExecutorsFrom(this.testExecutor, testUser, Lists.newArrayList(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A")); //TODO is the use of these Utils actually testing anything or sharing failure/bugs?
        Assert.assertEquals(1, getA.size());
        assertEquals(AddExecutorHandlerBasicTest.getInnerExecutor("A"), getA.get(0));
    }

    @Override
    protected void inspectReturnFromHandler(final Object value) {
        inspectReturnFromExecute(value);
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) {
        Assert.assertNull(value);
    }

    @Override
    protected Executor getTestExecutor() throws Exception {
        final Executor testExecutor = super.getTestExecutor();
        testExecutor.getConfig().addOperationHandler(RemoveExecutor.class, new RemoveExecutorHandler());

        try {
            final AddExecutorHandler basicHandler = new AddExecutorHandler();
            final AddExecutor addExecutor = new AddExecutor();

            basicHandler.doOperation(addExecutor.executor(AddExecutorHandlerBasicTest.getInnerExecutor("A")), this.context, testExecutor);
            basicHandler.doOperation(addExecutor.executor(AddExecutorHandlerBasicTest.getInnerExecutor("B")), this.context, testExecutor);
        } catch (Exception e) {
            throw new RuntimeException("Failed setUp of getTestExecutor", e);
        }

        return testExecutor;
    }

}
