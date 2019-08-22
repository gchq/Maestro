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

package uk.gov.gchq.maestro.operation.impl;

import junit.framework.TestCase;
import org.junit.Assert;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;


public class ForwardToRemoteExecutorHandlerTest extends MaestroHandlerBasicTest<ForwardToRemoteExecutorHandler> {

    @Override
    protected ForwardToRemoteExecutorHandler getTestHandler() throws Exception {
        return new ForwardToRemoteExecutorHandler();
    }

    @Override
    protected Operation getBasicOp() throws Exception {
        return new Operation("DefaultOperation").operationArg(Executor.WRAPPED_OP, new Operation("test")
                .operationArg(ForwardToRemoteExecutorHandler.OUTPUT_TYPE_REFERENCE, new TypeReferenceImpl.Integer()));
    }

    @Override
    protected Executor getTestExecutor() throws Exception {
        final Executor testExecutor = super.getTestExecutor();
        testExecutor.getConfig().addOperationHandler(Executor.DEFAULT_OPERATION, new ForwardToRemoteExecutorHandler());
        testExecutor.getConfig().addOperationHandler(Executor.INITIALISER, new ProxyInitialiseHandler()/*TODO*/);
        return testExecutor;
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
        TestCase.assertNotNull(value);
    }

    @Override
    protected void inspectFields() throws Exception {
        Assert.fail("Inspect remote executor action");
    }

    @Override
    protected Class<ForwardToRemoteExecutorHandler> getTestObjectClass() {
        return null;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.impl.ForwardToRemoteExecutorHandler\"\n" +
                "}";
    }

}
