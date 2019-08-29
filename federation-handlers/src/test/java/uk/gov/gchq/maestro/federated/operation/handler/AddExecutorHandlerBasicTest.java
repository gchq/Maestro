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

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.federated.operation.AddExecutor;
import uk.gov.gchq.maestro.federated.util.Config;
import uk.gov.gchq.maestro.federated.util.FederatedPropertiesUtil;
import uk.gov.gchq.maestro.helper.MaestroHandlerBasicTest;

import java.util.Collection;

public class AddExecutorHandlerBasicTest extends MaestroHandlerBasicTest<AddExecutor, AddExecutorHandler> {

    public static final String INNER_EXECUTOR_ID = "innerExecutorId";

    @Override
    protected AddExecutor getBasicOp() {
        return new AddExecutor()
                .executor(getInnerExecutor("A")); //TODO Improve the complexity of whats being added + test result.
    }

    public static Executor getInnerExecutor(final String s) {
        return new Executor().config(new Config().id(INNER_EXECUTOR_ID + s));
    }

    @Override
    protected AddExecutorHandler getBasicHandler() {
        return new AddExecutorHandler();
    }

    @Override
    protected void inspectFields() throws Exception {
        final Config config = testExecutor.getConfig();
        Assert.assertNotNull(config);
        final FederatedExecutorStorage value = FederatedPropertiesUtil.getDeserialisedExecutorStorage(config.getProperties());
        Assert.assertNotNull("expected value is null", value);
        final Collection<Executor> all = value.getAll(testUser);
        Assert.assertNotNull(all);
        Assert.assertEquals(1, all.size());
        assertEquals(INNER_EXECUTOR_ID + "A", all.iterator().next().getConfig().getId());
    }


    @Override
    protected void inspectReturnFromHandler(final Object value) {
        inspectReturnFromExecute(value);
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) {
        Assert.assertNull(value);
    }

    @Override
    protected Executor getTestExecutor() throws Exception {
        final Executor testExecutor = super.getTestExecutor();
        testExecutor.getConfig().addOperationHandler(AddExecutor.class, getBasicHandler());
        return testExecutor;
    }

}
