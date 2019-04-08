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
import org.junit.Test;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.util.Config;

import static org.junit.Assert.fail;

public abstract class MaestroHandlerTest<Op extends Operation, H extends OperationHandler<Op>> {
    public static final String EXECUTOR_ID = "testExecutorId";
    protected Executor testExecutor;

    @Before
    public void setUp() throws Exception {
        testExecutor = getTestExecutor();
    }

    @Test
    public void shouldHandler() throws Exception {
        final Op op = getOp();
        final H handler = getHandler();
        final Object handlerValue = handler.doOperation(op, new Context(), testExecutor);
        inspectReturnFromHandler(handlerValue);
        inspectFields();
    }

    @Test
    public void shouldExecute() throws Exception {
        final Op op = getOp();
        final Object executeValue = testExecutor.execute(op, new Context());
        inspectReturnFromExecute(executeValue);
        inspectFields();
    }

    protected abstract H getHandler() throws Exception;

    protected abstract Op getOp() throws Exception;

    protected void inspectFields() throws Exception {
        fail("Override test method: inspectFields");
    }

    protected Executor getTestExecutor() throws Exception {
        return new Executor().config(new Config(EXECUTOR_ID));
    }

    protected void inspectReturnFromHandler(final Object value) throws Exception {
        fail("Override test method: inspectReturnFromHandler");
    }

    protected void inspectReturnFromExecute(final Object value) throws Exception {
        fail("Override test method: inspectReturnFromExecute");
    }
}
