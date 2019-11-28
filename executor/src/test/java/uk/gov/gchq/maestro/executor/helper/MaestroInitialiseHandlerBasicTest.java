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

import org.junit.Test;

import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.handler.InitialiserHandler;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.Operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class MaestroInitialiseHandlerBasicTest<H extends InitialiserHandler> extends MaestroHandlerBasicTest<H> {

    @Test
    public void shouldHaveInitialiserId() throws Exception {
        final OperationHandler operationHandler = getExecutorConfig().getOperationHandler(new Operation(Executor.INITIALISER));
        assertNotNull(String.format("The Executor config has no handler mapped for %s", Executor.INITIALISER), operationHandler);
        assertEquals(String.format("test handler is not mapped to %s", Executor.INITIALISER), getTestHandler(), operationHandler);
    }

    @Override
    protected void inspectFields() throws Exception {
        //empty
    }

    @Override
    protected void inspectReturnFromHandler(final Object value) throws Exception {
        //empty
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
        //empty
    }
}
