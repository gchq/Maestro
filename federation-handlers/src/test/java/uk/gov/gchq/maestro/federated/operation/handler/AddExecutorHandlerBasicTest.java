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

import org.junit.Assert;

import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.federated.handler.AddExecutorHandler;
import uk.gov.gchq.maestro.federated.util.ExecutorStorageFederatedUtil;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AddExecutorHandlerBasicTest extends MaestroHandlerBasicTest<AddExecutorHandler> {

    public static final String INNER_EXECUTOR_ID = "innerExecutorId";

    @Override
    protected Operation getBasicOp() {
        return new Operation("AddExecutor")
                .operationArg(AddExecutorHandler.EXECUTOR, new Executor(getInnerConfig("A"))); //TODO Improve the complexity of whats being added + test result.
    }

    public static Config getInnerConfig(final String s) {
        return new Config(INNER_EXECUTOR_ID + s);
    }

    @Override
    protected AddExecutorHandler getTestHandler() {
        return new AddExecutorHandler();
    }

    @Override
    protected void inspectFields() throws Exception {
        final FederatedExecutorStorage value = ExecutorStorageFederatedUtil.getExecutorStorage(testExecutor);
        assertNotNull("expected value is null", value);
        final Collection<Executor> all = value.getAll(testUser);
        assertNotNull(all);
        assertEquals(1, all.size());
        assertEquals(INNER_EXECUTOR_ID + "A", all.iterator().next().getId());
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) {
        Assert.assertNull(value);
    }

    @Override
    protected Class<AddExecutorHandler> getTestObjectClass() {
        return AddExecutorHandler.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.federated.handler.AddExecutorHandler\",\n" +
                "  \"fieldDeclaration\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration\",\n" +
                "    \"fields\" : {\n" +
                "      \"auths\" : \"java.util.Set\",\n" +
                "      \"disabledByDefault\" : \"java.lang.Boolean\",\n" +
                "      \"executor\" : \"uk.gov.gchq.maestro.executor.Executor\",\n" +
                "      \"isPublic\" : \"java.lang.Boolean\",\n" +
                "      \"parentConfigId\" : \"java.lang.String\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }
}
