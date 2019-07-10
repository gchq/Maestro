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

package uk.gov.gchq.maestro.operation.handler.named;

import com.google.common.collect.Lists;
import org.junit.Before;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.handler.named.cache.NamedOperationCache;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.DESCRIPTION;
import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.OPERATION_CHAIN;
import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.OPERATION_NAME;

public class AddNamedOperationHandlerBasicTest extends MaestroHandlerBasicTest<AddNamedOperationHandler> {
    public static final String NAMED_OPERATION = "NamedOperation";
    private NamedOperationCache mockCache;

    @Override
    @Before
    public void setUp() throws Exception {
        mockCache = mock(NamedOperationCache.class);
        super.setUp();
    }

    @Override
    protected AddNamedOperationHandler getTestHandler() throws Exception {
        return new AddNamedOperationHandler(mockCache);
    }

    @Override
    protected Operation getBasicOp() throws Exception {
        OperationChain parent = new OperationChain("opchain", Lists.newArrayList(
                new Operation("AddNamedOperation").operationArg("name", "child"),
                new Operation("ToArray")));

        return new Operation(NAMED_OPERATION)
                .operationArg(OPERATION_NAME, "testName")
                .operationArg(DESCRIPTION, "testDescription")
                .operationArg(OPERATION_CHAIN, parent);
    }

    @Override
    protected Class<AddNamedOperationHandler> getTestObjectClass() {
        return AddNamedOperationHandler.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler\",\n" +
                "  \"fieldDeclaration\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.operation.declaration.FieldDeclaration\",\n" +
                "    \"fieldDeclarations\" : {\n" +
                "      \"description\" : \"java.lang.String\",\n" +
                "      \"operationChain\" : \"uk.gov.gchq.maestro.operation.OperationChain\",\n" +
                "      \"operationName\" : \"java.lang.String\",\n" +
                "      \"overwriteFlag\" : \"java.lang.Boolean\",\n" +
                "      \"parameters\" : \"java.util.Map\",\n" +
                "      \"readAccessRoles\" : \"java.util.List\",\n" +
                "      \"score\" : \"java.lang.Integer\",\n" +
                "      \"writeAccessRoles\" : \"java.util.List\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected AddNamedOperationHandler getFullyPopulatedTestObject() throws Exception {
        return getTestHandler();
    }

    @Override
    protected Executor getTestExecutor() throws Exception {
        return super.getTestExecutor().addHandler(NAMED_OPERATION, getTestHandler());
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
        assertNull(value);
    }

    @Override
    /**
     * {@link AddNamedOperationHandler} tests the cache values.
     */
    protected void inspectFields() throws Exception {
        //do nothing
    }
}
