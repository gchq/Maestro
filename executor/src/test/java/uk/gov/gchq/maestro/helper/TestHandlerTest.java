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

package uk.gov.gchq.maestro.helper;

import org.junit.Before;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;

import java.util.HashMap;
import java.util.Map;

public class TestHandlerTest extends MaestroHandlerBasicTest<TestHandler> {

    @Override
    protected Executor getTestExecutor() throws Exception {
        final Executor testExecutor = super.getTestExecutor();
        final HashMap<String, OperationHandler> map = new HashMap<>(testExecutor.getOperationHandlerMap());
        map.put("TestHandler", new TestHandler());
        return testExecutor.operationHandlerMap(testExecutor.getOperationHandlerMap());
    }

    @Override
    protected TestHandler getBasicHandler() throws Exception {
        return new TestHandler();
    }

    @Override
    protected Operation getBasicOp() throws Exception {
        return new Operation("TestHandler");
    }

    @Override
    protected void inspectFields() throws Exception {

    }

    @Override
    protected void inspectReturnFromHandler(final Object value) throws Exception {

    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
    }
}