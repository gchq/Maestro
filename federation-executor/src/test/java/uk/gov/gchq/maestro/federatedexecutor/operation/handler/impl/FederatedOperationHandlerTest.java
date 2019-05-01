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

import uk.gov.gchq.koryphe.impl.binaryoperator.StringConcat;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.federatedexecutor.operation.AddExecutor;
import uk.gov.gchq.maestro.federatedexecutor.operation.FederatedOperation;
import uk.gov.gchq.maestro.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.helper.TestHandler;
import uk.gov.gchq.maestro.helper.TestOperation;
import uk.gov.gchq.maestro.util.Config;

import static org.junit.Assert.assertEquals;

public class FederatedOperationHandlerTest extends MaestroHandlerBasicTest<FederatedOperation, FederatedOperationHandler> {

    @Override
    protected FederatedOperationHandler getBasicHandler() throws Exception {
        return new FederatedOperationHandler();
    }

    @Override
    protected FederatedOperation getBasicOp() throws Exception {
        return new FederatedOperation()
                .operation(new TestOperation().field("OpField"))
                .ids(
                        AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A",
                        AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "C",
                        AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "B"
                )
                .mergeOperation(new StringConcat());
    }

    @Override
    protected void inspectFields() throws Exception {
    }

    @Override
    protected void inspectReturnFromHandler(final Object value) throws Exception {
        inspectReturnFromExecute(value);
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
        assertEquals("AHandler,OpField,CHandler,OpField,BHandler,OpField", value);
    }

    @Override
    protected Executor getTestExecutor() throws Exception {
        final Executor testExecutor = super.getTestExecutor();
        testExecutor.getConfig()
                .addOperationHandler(FederatedOperation.class, new FederatedOperationHandler());

        try {
            final AddExecutorHandler basicHandler = new AddExecutorHandler();
            final AddExecutor addExecutor = new AddExecutor();

            final Executor a = new Executor().config(new Config().id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A").addOperationHandler(TestOperation.class, new TestHandler().fieldHandler("A" + "Handler")));
            basicHandler.doOperation(addExecutor.executor(a), this.context, testExecutor);

            final Executor b = new Executor().config(new Config().id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "B").addOperationHandler(TestOperation.class, new TestHandler().fieldHandler("B" + "Handler")));
            basicHandler.doOperation(addExecutor.executor(b), this.context, testExecutor);

            final Executor c = new Executor().config(new Config().id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "C").addOperationHandler(TestOperation.class, new TestHandler().fieldHandler("C" + "Handler")));
            basicHandler.doOperation(addExecutor.executor(c), this.context, testExecutor);

            final Executor d = new Executor().config(new Config().id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "D").addOperationHandler(TestOperation.class, new TestHandler().fieldHandler("D" + "Handler")));
            basicHandler.doOperation(addExecutor.executor(d), this.context, testExecutor);

        } catch (Exception e) {
            throw new RuntimeException("Failed setUp", e);
        }

        return testExecutor;
    }
}
