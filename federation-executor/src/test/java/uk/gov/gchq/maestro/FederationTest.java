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
package uk.gov.gchq.maestro;

import uk.gov.gchq.maestro.helpers.MaestroObjectTest;
import uk.gov.gchq.maestro.util.Config;

public class FederationTest extends MaestroObjectTest<Executor> {

    @Override
    protected Class getTestObjectClass() {
        return Executor.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.Executor\",\n" +
                "  \"config\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.util.Config\",\n" +
                "    \"operationHandlers\" : { },\n" +
                "    \"hooks\" : [ ],\n" +
                "    \"properties\" : {\n" +
                "      \"maestro.store.properties.class\" : \"uk.gov.gchq.maestro.ExecutorProperties\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Executor getTestObject() {
        final Executor executor = new Executor()
                .config(new Config());

        return executor;
    }
}
