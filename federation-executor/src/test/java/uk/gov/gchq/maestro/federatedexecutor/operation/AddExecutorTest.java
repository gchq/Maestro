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

package uk.gov.gchq.maestro.federatedexecutor.operation;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.helper.MaestroObjectTest;
import uk.gov.gchq.maestro.util.Config;

import java.util.HashMap;
import java.util.HashSet;

public class AddExecutorTest extends MaestroObjectTest<AddExecutor> {

    @Override
    protected Class<AddExecutor> getTestObjectClass() {
        return AddExecutor.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.federatedexecutor.operation.AddExecutor\",\n" +
                "  \"executor\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.Executor\",\n" +
                "    \"config\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.util.Config\",\n" +
                "      \"id\" : \"idValue1\",\n" +
                "      \"operationHandlers\" : { },\n" +
                "      \"properties\" : { },\n" +
                "      \"operationHooks\" : [ ],\n" +
                "      \"requestHooks\" : [ ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"executorAuths\" : [ \"auth1\", \"auth3\", \"auth2\" ],\n" +
                "  \"options\" : {\n" +
                "    \"op2\" : \"val2\",\n" +
                "    \"op1\" : \"val1\"\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected AddExecutor getTestObject() {
        final HashSet<String> auths = new HashSet<>();
        auths.add("auth1");
        auths.add("auth2");
        auths.add("auth3");

        final HashMap<String, String> options = new HashMap<>();
        options.put("op1", "val1");
        options.put("op2", "val2");

        return new AddExecutor()
                .auths(auths)
                .disabledByDefault(false)
                .options(options)
                .executor(new Executor().config(new Config().id("idValue1")));
    }
}
