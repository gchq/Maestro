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

import com.google.common.collect.Sets;
import org.junit.Test;

import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.OperationTest;
import uk.gov.gchq.maestro.operation.named.ParameterDetail;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class AddNamedOperationTest extends OperationTest {
    public static final String USER = "User";
    private static final OperationChain OPERATION_CHAIN =
            new OperationChain("ToList", new Operation("ToList").input(4), null, null);

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
                "      \"id\" : \"ToList\",\n" +
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


    @Override
    protected Set<String> getRequiredFields() {
        return Sets.newHashSet("operationChain", "operationName");
    }
}
