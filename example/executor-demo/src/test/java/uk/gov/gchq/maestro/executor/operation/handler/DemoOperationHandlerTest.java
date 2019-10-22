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
package uk.gov.gchq.maestro.executor.operation.handler;

import uk.gov.gchq.maestro.executor.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;
import uk.gov.gchq.maestro.rest.service.v2.OperationServiceV2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class DemoOperationHandlerTest extends MaestroHandlerBasicTest<DemoOperationHandler> {

    public static final String OPERATION_A = "operationA";
    public static final Integer MULTIPLIED = 2;

    @Override
    protected DemoOperationHandler getTestHandler() throws Exception {
        return new DemoOperationHandler(MULTIPLIED);
    }

    @Override
    protected Operation getBasicOp() throws Exception {
        return new Operation(OPERATION_A)
                .operationArg(DemoOperationHandler.VALUE, 2)
                .operationArg(OperationServiceV2.OUTPUT_TYPE_REFERENCE, new TypeReferenceImpl.Map());
    }

    @Override
    protected Class<DemoOperationHandler> getTestObjectClass() {
        return DemoOperationHandler.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.executor.operation.handler.DemoOperationHandler\",\n" +
                "  \"multiply\" : 2,\n" +
                "  \"fieldDeclaration\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration\",\n" +
                "    \"fields\" : {\n" +
                "      \"outputTypeReference\" : \"uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl$Map\",\n" +
                "      \"value\" : \"java.lang.Integer\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected void inspectFields() throws Exception {
        //nothing
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
        assertTrue(value instanceof String);
        final String s = (String) value;
        assertEquals(String.format(DemoOperationHandler.S_WAS_SUPPORTED_BY_EXECUTOR_S_D_MULTIPLIED_BY_D_IS_D, OPERATION_A, EXECUTOR_ID, 2, MULTIPLIED, 4), s);

    }


}
