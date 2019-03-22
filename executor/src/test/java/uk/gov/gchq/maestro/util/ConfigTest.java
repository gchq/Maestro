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

import org.junit.Test;

import uk.gov.gchq.maestro.StoreProperties;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.helpers.MaestroObjectTest;
import uk.gov.gchq.maestro.helpers.TestHandler;
import uk.gov.gchq.maestro.helpers.TestHook;
import uk.gov.gchq.maestro.helpers.TestOperation;
import uk.gov.gchq.maestro.library.NoLibrary;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclaration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
                "    \"maestro.store.properties.class\" : \"uk.gov.gchq.maestro.StoreProperties\"\n" +
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

    @Test
    public void shouldJsonSerialiseAndDeserialise() throws SerialisationException {
        final StoreProperties properties = new StoreProperties();
        properties.set("configKey", "configValue");
        final Config config = new Config.Builder()
                .id("testId")
                .operationHandler(new OperationDeclaration.Builder()
                        .handler(new TestHandler().fieldHandler("handlerFieldValue1"))
                        .operation(TestOperation.class)
                        .build())
                .storeProperties(properties)
                .addHook(new TestHook("testFieldVal"))
                .library(new NoLibrary())
                .build();

        byte[] serialisedConfig = JSONSerialiser.serialise(config);
        Config deserialisedConfig = JSONSerialiser.deserialise(serialisedConfig, Config.class);

        assertEquals(deserialisedConfig.getId(), config.getId());
        assertEquals(deserialisedConfig.getDescription(), config.getDescription());
        assertEquals(deserialisedConfig.getHooks(), config.getHooks());
        assertNotNull(deserialisedConfig.getLibrary());
        assertEquals(deserialisedConfig.getOperationHandlers(), config.getOperationHandlers());
        assertEquals(deserialisedConfig.getProperties(), config.getProperties());
    }

    @Override
    protected Class<Config> getTestObjectClass() {
        return Config.class;
    }
}