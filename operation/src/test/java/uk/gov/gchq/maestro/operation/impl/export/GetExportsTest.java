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

package uk.gov.gchq.maestro.operation.impl.export;

import uk.gov.gchq.maestro.operation.OperationTest;
import uk.gov.gchq.maestro.operation.Operation;


public class GetExportsTest extends OperationTest {
    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "  \"id\" : \"GetExports\",\n" +
                "  \"operationArgs\" : {\n" +
                "    \"exports\" : [ \"[Luk.gov.gchq.maestro.operation.Operation;\", [ {\n" + //TODO Luk
                "      \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "      \"id\" : \"GetSetExport\",\n" +
                "      \"operationArgs\" : {\n" +
                "        \"key\" : \"key1\"\n" +
                "      }\n" +
                "    }, {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "      \"id\" : \"GetSetExport\",\n" +
                "      \"operationArgs\" : {\n" +
                "        \"key\" : \"key2\"\n" +
                "      }\n" +
                "    } ] ]\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Operation getFullyPopulatedTestObject() throws Exception {
        return new Operation("GetExports")
                .operationArg("exports", new Operation[]{
                        new Operation("GetSetExport")
                                .operationArg("key","key1")
                                ,
                        new Operation("GetSetExport")
                                .operationArg("key","key2")
                                });
    }

}
