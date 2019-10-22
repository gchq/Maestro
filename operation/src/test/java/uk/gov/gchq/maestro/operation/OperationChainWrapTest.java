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

public class OperationChainWrapTest extends OperationTest {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.OperationChain\",\n" +
                "  \"id\" : \"chainWrap\",\n" +
                "  \"operations\" : [ {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "    \"id\" : \"innerOperation\",\n" +
                "    \"operationArgs\" : {\n" +
                "      \"input\" : [ \"[Ljava.lang.Object;\", [ \"value1\", \"value2\" ] ]\n" +
                "    }\n" +
                "  } ],\n" +
                "  \"operationArgs\" : {\n" +
                "    \"input\" : [ \"[Ljava.lang.Object;\", [ \"value1\", \"value2\" ] ]\n" + //TODO review this duplication of Operation Args in JsonString. See OperationChain#wrap
                "  }\n" +
                "}";
    }

    @Override
    protected Operation getFullyPopulatedTestObject() throws Exception {
        return OperationChain.wrap("chainWrap",
                new Operation("innerOperation")
                        .input(new Object[]{"value1", "value2"})
        );
    }
}
