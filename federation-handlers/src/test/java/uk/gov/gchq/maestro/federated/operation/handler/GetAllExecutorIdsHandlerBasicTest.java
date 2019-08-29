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

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.federated.operation.AddExecutor;
import uk.gov.gchq.maestro.federated.operation.GetAllExecutorIds;
import uk.gov.gchq.maestro.helper.MaestroHandlerBasicTest;

import java.util.ArrayList;
import java.util.Collection;

public class GetAllExecutorIdsHandlerBasicTest extends MaestroHandlerBasicTest<GetAllExecutorIds, GetAllExecutorIdsHandler> {

    @Override
    protected GetAllExecutorIdsHandler getBasicHandler() throws Exception {
        return new GetAllExecutorIdsHandler();
    }

    @Override
    protected GetAllExecutorIds getBasicOp() throws Exception {
        return new GetAllExecutorIds();
    }

    @Override
    protected void inspectReturnFromHandler(final Object value) throws Exception {
        inspect(value);
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
        inspect(value);
    }

    private void inspect(final Object value) {
        Assert.assertNotNull(value);
        Assert.assertTrue(value instanceof Collection);
        final ArrayList<Object> list = Lists.newArrayList((Iterable<Object>) value);
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.contains(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "A"));
        Assert.assertTrue(list.contains(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + "B"));
    }

    @Override
    protected void inspectFields() throws Exception {
        //empty
    }

    @Override
    protected Executor getTestExecutor() throws Exception {
        final Executor testExecutor = super.getTestExecutor();
        testExecutor.getConfig().addOperationHandler(GetAllExecutorIds.class, new GetAllExecutorIdsHandler());

        try {
            final AddExecutorHandler basicHandler = new AddExecutorHandler();
            final AddExecutor addExecutor = new AddExecutor();

            basicHandler.doOperation(addExecutor.executor(AddExecutorHandlerBasicTest.getInnerExecutor("A")), this.context, testExecutor);
            basicHandler.doOperation(addExecutor.executor(AddExecutorHandlerBasicTest.getInnerExecutor("B")), this.context, testExecutor);
        } catch (Exception e) {
            throw new RuntimeException("Failed setUp of getTestExecutor", e);
        }

        return testExecutor;
    }

}
