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

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.MaestroObjectTest;
import uk.gov.gchq.maestro.StoreProperties;
import uk.gov.gchq.maestro.TestHandler;
import uk.gov.gchq.maestro.TestOperation;

import static org.junit.Assert.*;

public class ConfigTest extends MaestroObjectTest<Config> {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"operationHandlers\" : {\n" +
                "    \"uk.gov.gchq.maestro.TestOperation\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.TestHandler\",\n" +
                "      \"handlerField\" : \"handlerFieldValue1\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"hooks\" : [ ],\n" +
                "  \"properties\" : {\n" +
                "    \"properties\" : {\n" +
                "      \"configKey\" : \"configValue\",\n" +
                "      \"maestro.store.properties.class\" : \"uk.gov.gchq.maestro.StoreProperties\"\n" +
                "    },\n" +
                "    \"jobTrackerEnabled\" : false,\n" +
                "    \"storePropertiesClassName\" : \"uk.gov.gchq.maestro.StoreProperties\",\n" +
                "    \"storePropertiesClass\" : \"uk.gov.gchq.maestro.StoreProperties\",\n" +
                "    \"jobExecutorThreadCount\" : 50,\n" +
                "    \"adminAuth\" : \"\"\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Config getTestObject() {
        final Config config = new Config();
        config.addOperationHandler(TestOperation.class, new TestHandler().fieldHandler("handlerFieldValue1"));
        final StoreProperties properties = new StoreProperties();
        properties.set("configKey", "configValue");
        config.setProperties(properties);
        return config;
    }

    @Override
    protected Class<Config> getTestObjectClass() {
        return Config.class;
    }
}