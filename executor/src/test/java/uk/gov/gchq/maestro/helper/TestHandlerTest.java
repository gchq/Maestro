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

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.operation.Operation;

import static org.junit.Assert.assertEquals;

public class TestHandlerTest extends MaestroHandlerBasicTest<TestHandler> {

    public static final String TEST_OPERATION = "TestOperation";

    @Override
    protected Executor getTestExecutor() throws Exception {
        return super.getTestExecutor()
                .addHandler(TEST_OPERATION, getTestHandler());
    }

    @Override
    protected TestHandler getTestHandler() throws Exception {
        return new TestHandler().handlerField("handlerValue1");
    }

    @Override
    protected Operation getBasicOp() throws Exception {
        return new Operation(TEST_OPERATION)
                .operationArg(TestHandler.FIELD, "operationValue1");
    }

    @Override
    protected void inspectFields() throws Exception {

    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
        assertEquals("handlerValue1,operationValue1", value);
    }

    @Override
    protected Class<TestHandler> getTestObjectClass() {
        return TestHandler.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.helper.TestHandler\",\n" +
                "  \"fieldDeclaration\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.operation.declaration.FieldDeclaration\",\n" +
                "    \"fieldDeclarations\" : {\n" +
                "      \"field\" : \"java.lang.String\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"handlerField\" : \"handlerValue1\"\n" +
                "}";
    }

    @Override
    protected TestHandler getFullyPopulatedTestObject() throws Exception {
        return getTestHandler();
    }
}
