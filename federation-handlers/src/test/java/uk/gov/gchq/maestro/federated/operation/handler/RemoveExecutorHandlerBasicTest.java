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
import org.junit.Assert;
import org.junit.Before;

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.federated.handler.AddExecutorHandler;
import uk.gov.gchq.maestro.federated.handler.RemoveExecutorHandler;
import uk.gov.gchq.maestro.federated.util.GetExecutorsFederatedUtil;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RemoveExecutorHandlerBasicTest extends MaestroHandlerBasicTest<RemoveExecutorHandler> {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        try {
            requireNonNull(testExecutor);
            final AddExecutorHandler addHandler = new AddExecutorHandler();
            final Operation addExecutor = new Operation("AddExecutor");

            addHandler.doOperation(addExecutor.operationArg(AddExecutorHandler.EXECUTOR, new Executor(AddExecutorHandlerBasicTest.getInnerConfig("A"))), this.context, testExecutor);
            addHandler.doOperation(addExecutor.operationArg(AddExecutorHandler.EXECUTOR, new Executor(AddExecutorHandlerBasicTest.getInnerConfig("B"))), this.context, testExecutor);
        } catch (Exception e) {
            throw new RuntimeException("Failed setUp of getTestExecutor", e);
        }
    }

    @Override
    protected RemoveExecutorHandler getTestHandler() throws Exception {
        return new RemoveExecutorHandler();
    }

    @Override
    protected Operation getBasicOp() {
        return new Operation("RemoveExecutor").operationArg(RemoveExecutorHandler.EXECUTOR_ID, AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "B");
    }

    @Override
    protected void inspectFields() throws Exception {
        final ArrayList<String> executorIds = Lists.newArrayList(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A", AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "B");
        try {
            final Collection<Executor> getAandB = GetExecutorsFederatedUtil.getExecutorsFrom(this.testExecutor, testUser, executorIds); //TODO Is this testing anything further than what Util is testing?
            Assert.fail("exception expected");
        } catch (MaestroCheckedException e) {
            assertTrue(e.getMessage().contains(String.format(FederatedExecutorStorage.ERROR_GETTING_S_FROM_FEDERATED_EXECUTOR_STORAGE_S, executorIds.toString(), "")));
        }
        final Collection<Executor> getA = GetExecutorsFederatedUtil.getExecutorsFrom(this.testExecutor, testUser, Lists.newArrayList(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A")); //TODO Is this testing anything further than what Util is testing?
        assertEquals(1, getA.size());
        assertEquals(new Executor(AddExecutorHandlerBasicTest.getInnerConfig("A")), getA.toArray()[0]);
    }

    @Override
    protected void inspectReturnFromHandler(final Object value) {
        inspectReturnFromExecute(value);
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) {
        assertTrue((Boolean) value);
    }

    @Override
    protected Class<RemoveExecutorHandler> getTestObjectClass() {
        return RemoveExecutorHandler.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.federated.handler.RemoveExecutorHandler\",\n" +
                "  \"fieldDeclaration\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration\",\n" +
                "    \"fields\" : {\n" +
                "      \"executorId\" : \"java.lang.String\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }
}
