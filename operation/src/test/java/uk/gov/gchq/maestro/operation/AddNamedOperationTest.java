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

package uk.gov.gchq.maestro.operation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import uk.gov.gchq.koryphe.impl.function.ToArray;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.named.operation.ParameterDetail;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationTest;
import uk.gov.gchq.maestro.operation.OperationChain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class AddNamedOperationTest extends OperationTest {
    public static final String USER = "User";
    private static final OperationChain OPERATION_CHAIN =
            new OperationChain("ToList", new Operation("ToList").input(4));

    @Test
    public void shouldJsonSerialiseAndDeserialiseWithNoOptions() throws Exception {
        final Operation operation = getFullyPopulatedTestObject().options(null);

        // When
        final byte[] json = toJson(operation);
        final Operation deserialisedObj = fromJson(json);

        assertFalse(new String(json).toLowerCase().contains("options"));
        assertNotNull(deserialisedObj);
    }

    @Override
    protected String getJSONString() {
        return String.format("{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "  \"id\" : \"AddNamedOperation\",\n" +
                "  \"operationArgs\" : {\n" +
                "    \"description\" : \"Test Named Operation\",\n" +
                "    \"operationChain\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.operation.OperationChain\",\n" +
                "      \"id\" : \"ToListChain\",\n" +
                "      \"operations\" : [ {\n" +
                "        \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "        \"id\" : \"ToList\",\n" +
                "        \"operationArgs\" : {\n" +
                "          \"input\" : 4\n" +
                "        }\n" +
                "      } ],\n" +
                "      \"operationArgs\" : { }\n" +
                "    },\n" +
                "    \"operationName\" : \"Test\",\n" +
                "    \"overwriteFlag\" : true,\n" +
                "    \"parameters\" : {\n" +
                "      \"class\" : \"java.util.HashMap\",\n" +
                "      \"testOption\" : {\n" +
                "        \"description\" : \"Description\",\n" +
                "        \"defaultValue\" : \"On\",\n" +
                "        \"valueClass\" : \"java.lang.String\",\n" +
                "        \"required\" : false\n" +
                "      }\n" +
                "    },\n" +
                "    \"readAccessRoles\" : \"User\",\n" +
                "    \"score\" : 0,\n" +
                "    \"writeAccessRoles\" : \"User\"\n" +
                "  }\n" +
                "}");
    }

    @Override
    protected Operation getFullyPopulatedTestObject() throws Exception {
        Map<String, ParameterDetail> parameters = new HashMap<>();
        parameters.put("testOption", new ParameterDetail("Description", String.class, false, "On", null));

        return new Operation("AddNamedOperation")
                .operationArg("operationChain", OPERATION_CHAIN)
                .operationArg("description", "Test Named Operation")
                .operationArg("operationName", "Test")
                .operationArg("readAccessRoles", USER)
                .operationArg("overwriteFlag", true)
                .operationArg("writeAccessRoles", USER)
                .operationArg("parameters", parameters)
                .operationArg("score", 0);
    }

    @Test
    public void shouldGetOperationsWithDefaultParameters() throws Exception {
        // Given
        final Operation addNamedOperation = new Operation("AddNamedOperation")
                .operationArg("operationChain", "{\"class\":\"uk.gov.gchq.maestro.operation.OperationChain\"," +
                        "\"id\":\"ToArrayChain\"," +
                        "\"operations\":[{\"class\": \"uk.gov.gchq.maestro.operation.Operation\", " +
                        "\"input\": [\"${testParameter}\"]," +
                        "\"id\":\"ToArray\"" +
                        "}]}")
                .operationArg("description", "Test Named Operation")
                .operationArg("operationName", "Test")
                .operationArg("readAccessRoles", USER)
                .operationArg("overwriteFlag", false)
                .operationArg("writeAccessRoles", USER)
                .operationArg("parameters", new ParameterDetail.Builder()
                        .description("test")
                        .defaultValue(1)
                        .valueClass(Integer.class)
                        .required(false)
                        .build())
                .operationArg("score", 2);
        // When
        final OperationChain operationChain = JSONSerialiser.deserialise((String) addNamedOperation.get("operationChain"), OperationChain.class); //TODO this Logic needs to be in handler.

        Collection<Operation> operations = operationChain.getOperations();

        // Then
        assertEquals(
                Collections.singletonList("ToArray"),
                operations.stream().map(o -> o.getId()).collect(Collectors.toList())
        );
        final Operation nestedOp = operations.iterator().next();
        final List<? extends Integer> input =
                Lists.newArrayList((Integer) nestedOp.get("Input"));
        assertEquals(Collections.singletonList(1), input); //TODO this logic needs to be in handler
    }

    @Test
    public void shouldGetOperationsWhenNoDefaultParameter() throws SerialisationException {
        final HashMap<String, ParameterDetail> parameters = new HashMap<>();
        parameters.put("testParameter", new ParameterDetail.Builder()
                .description("the seed")
                .valueClass(String.class)
                .required(false)
                .build());

        // Given
        final Operation addNamedOperation = new Operation("AddNamedOperation")
                .operationArg("operationChain",
                        "{\"class\":\"uk.gov.gchq.maestro.operation.OperationChain\"," +
                                "\"id\":\"ToArrayChain\"," +
                                "\"operations\":[{\"class\": \"uk.gov.gchq.maestro.operation.Operation\", " +
                                "\"input\": [4]," +
                                "\"id\":\"ToArray\"" +
                                "}]}")
                .operationArg("description", "Test Named Operation")
                .operationArg("name", "Test")
                .operationArg("overwrite", false)
                .operationArg("readAccessRoles", USER)
                .operationArg("writeAccessRoles", USER)
                .operationArg("parameter", parameters)
                .operationArg("score", 2);

        // When
        final OperationChain operationChain = JSONSerialiser.deserialise((String) addNamedOperation.get("operationChain"), OperationChain.class); //TODO this Logic needs to be in handler.

        Collection<Operation> operations = operationChain.getOperations();

        // Then
        assertEquals(
                Collections.singletonList("ToArray"),
                operations.stream().map(o -> o.getId()).collect(Collectors.toList())
        );
        final Operation nestedOp = operations.iterator().next();
        final List<? extends Integer> input =
                Lists.newArrayList((Integer) nestedOp.get("Input"));
        assertEquals(Collections.singletonList(4), input);
    }

    @Override
    protected Set<String> getRequiredFields() {
        return Sets.newHashSet("operationChain", "operationName");
    }
}
