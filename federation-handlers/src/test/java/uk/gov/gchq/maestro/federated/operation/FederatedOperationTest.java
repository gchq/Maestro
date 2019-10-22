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

package uk.gov.gchq.maestro.federated.operation;

import com.google.common.collect.Lists;

import uk.gov.gchq.koryphe.impl.binaryoperator.Max;
import uk.gov.gchq.maestro.federated.handler.FederatedOperationHandler;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.helper.MaestroObjectTest;

public class FederatedOperationTest extends MaestroObjectTest<Operation> {

    @Override
    protected Class<Operation> getTestObjectClass() {
        return Operation.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "  \"id\" : \"FederatedOperation\",\n" +
                "  \"operationArgs\" : {\n" +
                "    \"ids\" : [ \"java.util.ArrayList\", [ \"a\", \"c\", \"b\" ] ],\n" + //TODO should this get ordered?
                "    \"mergeOperation\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.koryphe.impl.binaryoperator.Max\"\n" +
                "    },\n" +
                "    \"operation\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "      \"id\" : \"TestOperation\",\n" +
                "      \"operationArgs\" : { }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Operation getFullyPopulatedTestObject() throws Exception {
        return new Operation("FederatedOperation")
                .operationArg(FederatedOperationHandler.OPERATION, new Operation("TestOperation"))
                .operationArg(FederatedOperationHandler.IDS, Lists.newArrayList("a", "c", "b"))
                .operationArg(FederatedOperationHandler.MERGE_OPERATION, new Max());
    }
}
