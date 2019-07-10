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

package uk.gov.gchq.maestro.operation.impl.output;

import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationTest;


public class ToSetTest extends OperationTest {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "  \"id\" : \"ToSet\",\n" +
                "  \"operationArgs\" : {\n" +
                "    \"input\" : [ \"[Ljava.lang.Object;\", [ \"1\", \"2\" ] ]\n" + //TODO [L
                "  }\n" +
                "}";
    }

    @Override
    protected Operation getFullyPopulatedTestObject() throws Exception {
        return new Operation("ToSet")
                .input(new Object[]{"1", "2"});
    }


}
