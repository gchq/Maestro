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

import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.helper.MaestroObjectTest;
import uk.gov.gchq.maestro.helper.TestHandler;
import uk.gov.gchq.maestro.helper.TestOperation;
import uk.gov.gchq.maestro.util.Config;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;


public class ExecutorTest extends MaestroObjectTest<Executor> {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.Executor\",\n" +
                "  \"config\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.util.Config\",\n" +
                "    \"operationHandlers\" : {\n" +
                "      \"uk.gov.gchq.maestro.helper.TestOperation\" : {\n" +
                "        \"class\" : \"uk.gov.gchq.maestro.helper.TestHandler\",\n" +
                "        \"handlerField\" : \"handlerFieldValue1\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"properties\" : {\n" +
                "      \"configKey\" : \"configValue\"\n" +
                "    },\n" +
                "    \"operationHooks\" : [ ],\n" +
                "    \"requestHooks\" : [ ]\n" +
                "  }\n" +
                "}";
    }

    @Override
    public void shouldJSONSerialise() throws uk.gov.gchq.maestro.commonutil.exception.SerialisationException {
        super.shouldJSONSerialise();
        final Executor testObject = getTestObject();

        final String executorString = getJSONString();
        requireNonNull(executorString);
        final byte[] serialise = uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser.serialise(testObject, true);
        assertEquals(executorString, new String(serialise));
        assertEquals(testObject, uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser.deserialise(serialise, getTestObjectClass()));
    }

    @Override
    protected Executor getTestObject() {
        final Config config = new Config();
        final ExecutorProperties properties = new ExecutorProperties();
        properties.set("configKey", "configValue");
        config.setProperties(properties);
        config.addOperationHandler(TestOperation.class, new TestHandler().fieldHandler("handlerFieldValue1"));
        return new Executor().config(config);
    }

    @Test
    public void shouldRunTestHandler() throws SerialisationException, OperationException {
        final byte[] serialise = JSONSerialiser.serialise(getTestObject(), true);

        final Executor executor = Executor.deserialise(serialise);
        final Object execute = executor.execute(new TestOperation().setField("opFieldValue1"), new Context());
        assertEquals("handlerFieldValue1,opFieldValue1", execute);
    }

    @Override
    protected Class<Executor> getTestObjectClass() {
        return Executor.class;
    }
}
