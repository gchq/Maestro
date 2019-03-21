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

package uk.gov.gchq.maestro.util;

import uk.gov.gchq.maestro.helpers.MaestroObjectTest;
import uk.gov.gchq.maestro.ExecutorProperties;
import uk.gov.gchq.maestro.helpers.TestHandler;
import uk.gov.gchq.maestro.helpers.TestOperation;

public class ConfigTest extends MaestroObjectTest<Config> {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"operationHandlers\" : {\n" +
                "    \"uk.gov.gchq.maestro.helpers.TestOperation\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.helpers.TestHandler\",\n" +
                "      \"handlerField\" : \"handlerFieldValue1\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"hooks\" : [ ],\n" +
                "  \"properties\" : {\n" +
                "    \"configKey\" : \"configValue\",\n" +
                "    \"maestro.store.properties.class\" : \"uk.gov.gchq.maestro.ExecutorProperties\"\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Config getTestObject() {
        final Config config = new Config();
        config.addOperationHandler(TestOperation.class, new TestHandler().fieldHandler("handlerFieldValue1"));
        final ExecutorProperties properties = new ExecutorProperties();
        properties.set("configKey", "configValue");
        config.setProperties(properties);
        return config;
    }

    @Override
    protected Class<Config> getTestObjectClass() {
        return Config.class;
    }
}