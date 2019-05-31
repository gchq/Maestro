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

package uk.gov.gchq.maestro.operation.impl.export.resultcache;

import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationTest;

import java.util.HashSet;


public class ExportToResultCacheTest extends OperationTest {
    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "  \"id\" : \"ExportToResultCache\",\n" +
                "  \"operationArgs\" : {\n" +
                "    \"input\" : \"input\",\n" +
                "    \"key\" : \"key\",\n" +
                "    \"opAuths\" : [ \"java.util.HashSet\", [ \"1\", \"2\" ] ]\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Operation getFullyPopulatedTestObject() throws Exception {
        final String key = "key";
        final HashSet<String> opAuths = Sets.newHashSet("1", "2");
        final String input = "input";
        return new Operation("ExportToResultCache")
                .operationArg("key", key)
                .operationArg("opAuths", opAuths)
                .operationArg("input", input);
    }
}

