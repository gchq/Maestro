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

package uk.gov.gchq.maestro.operation.impl;

import uk.gov.gchq.maestro.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HashMapHandlerTest extends MaestroHandlerBasicTest<HashMapHandler> {

    public static final String KEY_FROM_TEST = "keyFromTest";
    public static final String VALUE_FROM_TEST = "valueFromTest";
    public static final String ID = "hashmapOperation";
    private HashMap<Object, Object> delegateMap = new HashMap<>();

    @Override
    protected HashMapHandler getTestHandler() throws Exception {
        final HashMapHandler hashMapHandler = new HashMapHandler();
        hashMapHandler.setDelegateMap(delegateMap);
        return hashMapHandler;
    }

    @Override
    protected Operation getBasicOp() throws Exception {
        return new Operation(ID)
                .operationArg(HashMapHandler.MAP_COMMAND, HashMapHandler.COMMAND_PUT)
                .operationArg(HashMapHandler.MAP_KEY, KEY_FROM_TEST)
                .operationArg(HashMapHandler.MAP_VALUE, VALUE_FROM_TEST);
    }

    @Override
    protected Class<HashMapHandler> getTestObjectClass() {
        return HashMapHandler.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.impl.HashMapHandler\",\n" +
                "  \"delegateMap\" : { },\n" +
                "  \"fieldDeclaration\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.operation.declaration.FieldDeclaration\",\n" +
                "    \"fields\" : {\n" +
                "      \"mapCommand\" : \"java.lang.String\",\n" +
                "      \"mapGetKey\" : \"java.lang.Object\",\n" +
                "      \"mapKey\" : \"java.lang.Object\",\n" +
                "      \"mapValue\" : \"java.lang.Object\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected void inspectFields() throws Exception {
        final Operation operation = new Operation(ID)
                .operationArg(HashMapHandler.MAP_COMMAND, HashMapHandler.COMMAND_GET)
                .operationArg(HashMapHandler.MAP_KEY, KEY_FROM_TEST);

        final Object o = getTestHandler().doOperation(operation, context, testExecutor);

        assertEquals(VALUE_FROM_TEST, o);
    }

    @Override
    protected void inspectReturnFromHandler(final Object value) throws Exception {
        super.inspectReturnFromHandler(value);
    }

    @Override
    protected void inspectReturnFromExecute(final Object value) throws Exception {
        assertNull(value);
    }
}
