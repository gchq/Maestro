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

package uk.gov.gchq.maestro.executor.helper;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.helper.MaestroObjectTest;
import uk.gov.gchq.maestro.operation.user.User;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Abstract test to perform the initial basic form of testing for a handler.
 *
 * @param <H> The Handler
 * @see OperationHandler
 */
public abstract class MaestroHandlerBasicTest<H extends OperationHandler> extends MaestroObjectTest<H> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaestroHandlerBasicTest.class);
    public static final String EXECUTOR_ID = "testExecutorId";
    protected Executor testExecutor;
    protected Context context;
    protected User testUser;

    @Before
    public void setUp() throws Exception {
        testUser = new User("testUser");
        context = getContext();
        testExecutor = getTestExecutor();
        LOGGER.warn("Executing with {}", new String(JSONSerialiser.serialise(testExecutor)));
    }

    @Test
    public void shouldHandleABasicExample() throws Exception {
        final Operation op = getBasicOp();
        final H handler = getTestHandler();
        final Object handlerValue = handler.doOperation(op, context, testExecutor);
        inspectReturnFromHandler(handlerValue);
        inspectFields();
    }

    @Test
    public void shouldExecuteABasicExample() throws Exception {
        final Operation op = getBasicOp();
        final Object executeValue = testExecutor.execute(op, context);
        inspectReturnFromExecute(executeValue);
        inspectFields();
    }

    protected abstract H getTestHandler() throws Exception;

    protected abstract Operation getBasicOp() throws Exception;

    protected void inspectFields() throws Exception {
        fail("Override test method: inspectFields");
    }

    private Executor getTestExecutor() throws Exception {
        return new Executor(getExecutorConfig());
    }

    /**
     * This method contains helper to align the test operation to be handled automatically by the test handler.
     *
     * @return config
     * @throws Exception exception
     */
    protected Config getExecutorConfig() throws Exception {
        return new Config(EXECUTOR_ID)
                .addOperationHandler(getBasicOp().getId(), getTestHandler());
    }

    protected Context getContext() {
        return new Context(testUser);
    }

    protected void inspectReturnFromHandler(final Object value) throws Exception {
        try {
            inspectReturnFromExecute(value);
        } catch (AssertionError e) {
            fail("Override test method: inspectReturnFromHandler or " + e.getMessage());
        }
    }

    protected void inspectReturnFromExecute(final Object value) throws Exception {
        fail("Override test method: inspectReturnFromExecute");
    }

    @Override
    protected H getFullyPopulatedTestObject() throws Exception {
        return getTestHandler();
    }

    @Test
    public void shouldGetNonNullFieldDeclaration() throws Exception {
        assertNotNull(getTestHandler().getFieldDeclaration());
    }
}
