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

import org.junit.Assert;
import org.junit.Test;

import uk.gov.gchq.maestro.exception.SerialisationException;
import uk.gov.gchq.maestro.jsonserialisation.JSONSerialiser;

import java.util.HashMap;
import java.util.Map;


public class ExecutorTest extends MaestroObjectTest {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.Executor\",\n" +
                "  \"operationHandlerMap\" : {\n" +
                "    \"uk.gov.gchq.maestro.TestOperation\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.TestHandler\",\n" +
                "      \"handlerField\" : \"handlerFieldValue1\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"config\" : {\n" +
                "    \"configKey\" : \"configValue\"\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Executor getTestObject() {
        Map<Class<? extends DoGetOperation>, OperationHandler> operationHandlerMap = new HashMap<>();
        operationHandlerMap.put(TestOperation.class, new TestHandler().fieldHandler("handlerFieldValue1"));
        final Map<String, String> config = new HashMap<>();
        config.put("configKey", "configValue");
        return new Executor().operationHandlerMap(operationHandlerMap).config(config);
    }

    @Test
    public void shouldRunTestHandler() throws SerialisationException {
        final byte[] serialise = JSONSerialiser.serialise(getTestObject(), true);

        final Executor executor = Executor.deserialise(serialise);
        final String execute = executor.execute(new TestOperation().setField("opFieldValue1"), new Context());
        Assert.assertEquals("handlerFieldValue1,opFieldValue1", execute);
    }
}
