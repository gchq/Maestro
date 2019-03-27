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

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import uk.gov.gchq.maestro.ExecutorProperties;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.helper.MaestroObjectTest;
import uk.gov.gchq.maestro.helper.TestHandler;
import uk.gov.gchq.maestro.helper.TestHook;
import uk.gov.gchq.maestro.helper.TestOperation;
import uk.gov.gchq.maestro.library.NoLibrary;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclaration;
import uk.gov.gchq.maestro.util.hook.Hook;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConfigTest extends MaestroObjectTest<Config> {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"operationHandlers\" : {\n" +
                "    \"uk.gov.gchq.maestro.helper.TestOperation\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.helper.TestHandler\",\n" +
                "      \"handlerField\" : \"handlerFieldValue1\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"properties\" : {\n" +
                "    \"configKey\" : \"configValue\",\n" +
                "    \"maestro.executor.properties.class\" : \"uk.gov.gchq.maestro.ExecutorProperties\"\n" +
                "  },\n" +
                "  \"operationHooks\" : [ ],\n" +
                "  \"requestHooks\" : [ ]\n" +
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

    @Test
    public void shouldJsonSerialiseAndDeserialise() throws SerialisationException {
        final ExecutorProperties properties = new ExecutorProperties();
        properties.set("configKey", "configValue");
        final Config config = new Config.Builder()
                .id("testId")
                .operationHandler(new OperationDeclaration.Builder()
                        .handler(new TestHandler().fieldHandler("handlerFieldValue1"))
                        .operation(TestOperation.class)
                        .build())
                .executorProperties(properties)
                .addRequestHook(new TestHook("testFieldVal"))
                .library(new NoLibrary())
                .build();

        byte[] serialisedConfig = JSONSerialiser.serialise(config);
        Config deserialisedConfig = JSONSerialiser.deserialise(serialisedConfig, Config.class);

        assertEquals(deserialisedConfig.getId(), config.getId());
        assertEquals(deserialisedConfig.getDescription(), config.getDescription());
        assertEquals(deserialisedConfig.getRequestHooks(), config.getRequestHooks());
        assertNotNull(deserialisedConfig.getLibrary());
        assertEquals(deserialisedConfig.getOperationHandlers(), config.getOperationHandlers());
        assertEquals(deserialisedConfig.getProperties(), config.getProperties());
    }

    @Test
    public void shouldBuildConfigCorrectly() {
        // Given
        final ExecutorProperties mergedProperties = new ExecutorProperties();
        mergedProperties.getProperties().put("key2", "value2");
        mergedProperties.getProperties().put("key1", "value1");
        mergedProperties.getProperties().put("testKey", "value1");
        final ExecutorProperties testProperties = new ExecutorProperties();
        testProperties.getProperties().put("key2", "value2");
        final Hook testOpHook = new TestHook("field1Val1");
        final Hook testReqHook = new TestHook("field1Val2");
        final OperationDeclaration testOpDeclaration =
                new OperationDeclaration.Builder()
                        .operation(TestOperation.class)
                        .handler(new TestHandler())
                        .build();

        // When
        final Config config = new Config.Builder()
                .operationHandler(testOpDeclaration)
                .executorProperties(StreamUtil.executorProps(getClass()))
                .executorProperties(testProperties)
                .addOperationHook(testOpHook)
                .addRequestHook(testReqHook)
                .build();

        // Then
        assertEquals(ImmutableMap.of(testOpDeclaration.getOperation(), testOpDeclaration.getHandler()), config.getOperationHandlers());
        assertEquals(mergedProperties, config.getProperties());
        assertEquals(Arrays.asList(testOpHook), config.getOperationHooks());
        assertEquals(Arrays.asList(testReqHook), config.getRequestHooks());
    }

    @Override
    protected Class<Config> getTestObjectClass() {
        return Config.class;
    }
}