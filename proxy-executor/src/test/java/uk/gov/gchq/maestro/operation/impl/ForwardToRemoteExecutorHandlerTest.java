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
import uk.gov.gchq.maestro.helper.TestOperation;
import uk.gov.gchq.maestro.operation.DefaultOperation;
import uk.gov.gchq.maestro.operation.impl.initialisation.Initialiser;


public class ForwardToRemoteExecutorHandlerTest extends MaestroHandlerBasicTest<DefaultOperation, ForwardToRemoteExecutorHandler> {

    @Override
    protected ForwardToRemoteExecutorHandler getBasicHandler() throws Exception {
        return new ForwardToRemoteExecutorHandler();
    }

    @Override
    protected DefaultOperation getBasicOp() throws Exception {
        return new DefaultOperation().setWrappedOp(new TestOperation());
    }

    @Override
    protected Executor getTestExecutor() throws Exception {
        final Executor testExecutor = super.getTestExecutor();
        testExecutor.getConfig().addOperationHandler(DefaultOperation.class, new ForwardToRemoteExecutorHandler());
        testExecutor.getConfig().addOperationHandler(Initialiser.class, new TestProxyInitialiseHandler()/*TODO make sure the real one is appropriately tested not just test*/);
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
}