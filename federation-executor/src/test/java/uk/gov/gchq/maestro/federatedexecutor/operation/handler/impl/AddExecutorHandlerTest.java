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

import org.junit.Assert;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.federatedexecutor.operation.AddExecutor;
import uk.gov.gchq.maestro.helper.MaestroHandlerTest;
import uk.gov.gchq.maestro.util.Config;
import uk.gov.gchq.maestro.util.FederatedUtil;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AddExecutorHandlerTest extends MaestroHandlerTest<AddExecutor, AddExecutorHandler> {

    public static final String INNER_EXECUTOR_ID = "innerExecutorId";

    @Override
    protected AddExecutor getOp() {
        return new AddExecutor()
                .config(new Config().id(INNER_EXECUTOR_ID));
    }

    @Override
    protected AddExecutorHandler getHandler() {
        return new AddExecutorHandler();
    }

    @Override
    protected void inspectFields() {
        final Config config = testExecutor.getConfig();
        assertNotNull(config);
        final String key = FederatedUtil.EXECUTOR_STORE + AddExecutorHandlerTest.INNER_EXECUTOR_ID;
        final Object value = config.getProperties().getProperties().get(key);
        assertNotNull("expected value is null", value);
        assertTrue(value instanceof Executor);
        Assert.assertEquals(AddExecutorHandlerTest.INNER_EXECUTOR_ID, ((Executor) value).getConfig().getId());
    }


    @Override
    protected void inspectReturnFromHandler(final Object value) {
        inspectReturnFromExecute(value);
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) {
        assertNull(value);
    }
}