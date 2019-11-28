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

import org.junit.Before;

import uk.gov.gchq.koryphe.impl.binaryoperator.StringConcat;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.executor.helper.TestHandler;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.handler.AddExecutorHandler;
import uk.gov.gchq.maestro.federated.handler.FederatedOperationHandler;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FederatedOperationHandlerTest extends MaestroHandlerBasicTest<FederatedOperationHandler> {


    public static final String TEST_OPERATION = "testOperation";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        try {
            final AddExecutorHandler basicHandler = new AddExecutorHandler();
            final Operation addExecutor = new Operation("addExecutor");

            addConfigToExecutor(basicHandler, addExecutor, "A");
            addConfigToExecutor(basicHandler, addExecutor, "B");
            addConfigToExecutor(basicHandler, addExecutor, "C");
            final Config inner = new Config()
                    //This operation will error.
                    .id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "D");
            basicHandler.doOperation(addExecutor.operationArg(AddExecutorHandler.EXECUTOR, new Executor(inner)), this.context, testExecutor);

        } catch (Exception e) {
            throw new RuntimeException("Failed setUp", e);
        }

    }

    @Override
    protected FederatedOperationHandler getTestHandler() throws Exception {
        return new FederatedOperationHandler();
    }

    @Override
    protected Operation getBasicOp() throws Exception {
        final List<String> value = new ArrayList<>();
        value.add(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A");
        value.add(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "C");
        value.add(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "B");

        return new Operation("federatedOperation")
                .operationArg(FederatedOperationHandler.OPERATION, new Operation(TEST_OPERATION)
                        .operationArg("field", "OpField"))
                .operationArg(FederatedOperationHandler.IDS, value)
                .operationArg(FederatedOperationHandler.MERGE_OPERATION, new StringConcat());
    }

    @Override
    protected void inspectFields() throws Exception {
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
        assertEquals("AHandler,OpField,CHandler,OpField,BHandler,OpField", value);
    }

    private void addConfigToExecutor(final AddExecutorHandler addExecutorHandler, final Operation addExecutor, final String id) throws OperationException {
        final Config inner = new Config()
                .id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + id)
                .addOperationHandler(TEST_OPERATION, new TestHandler().handlerField(id + "Handler"));
        addExecutorHandler.doOperation(addExecutor.operationArg(AddExecutorHandler.EXECUTOR, new Executor(inner)), this.context, testExecutor);
    }

    @Override
    protected Class<FederatedOperationHandler> getTestObjectClass() {
        return FederatedOperationHandler.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.federated.handler.FederatedOperationHandler\",\n" +
                "  \"fieldDeclaration\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration\",\n" +
                "    \"fields\" : {\n" +
                "      \"ids\" : \"java.util.List\",\n" +
                "      \"mergeOperation\" : \"uk.gov.gchq.koryphe.binaryoperator.KorypheBinaryOperator\",\n" +
                "      \"operation\" : \"uk.gov.gchq.maestro.operation.Operation\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }
}
