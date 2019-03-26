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

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.helpers.MaestroObjectTest;
import uk.gov.gchq.maestro.util.Config;
import uk.gov.gchq.maestro.util.FederatedUtil;

import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class FederationShouldStoreExecutorsTest extends MaestroObjectTest<Executor> {

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
                "    \"properties\" : {\n" +
                "      \"ExecutorStore_Executor1\" : \"{\\\"class\\\":\\\"uk.gov.gchq.maestro.Executor\\\",\\\"config\\\":{\\\"class\\\":\\\"uk.gov.gchq.maestro.util.Config\\\",\\\"id\\\":\\\"ExecutorId1\\\",\\\"operationHandlers\\\":{},\\\"properties\\\":{\\\"maestro.store.properties.class\\\":\\\"uk.gov.gchq.maestro.ExecutorProperties\\\"},\\\"operationHooks\\\":[],\\\"requestHooks\\\":[]}}\",\n" +
                "      \"ExecutorStore_Executor2\" : \"{\\\"class\\\":\\\"uk.gov.gchq.maestro.Executor\\\",\\\"config\\\":{\\\"class\\\":\\\"uk.gov.gchq.maestro.util.Config\\\",\\\"id\\\":\\\"ExecutorId2\\\",\\\"operationHandlers\\\":{},\\\"properties\\\":{\\\"maestro.store.properties.class\\\":\\\"uk.gov.gchq.maestro.ExecutorProperties\\\"},\\\"operationHooks\\\":[],\\\"requestHooks\\\":[]}}\"\n" +
                "    },\n" +
                "    \"operationHooks\" : [ ],\n" +
                "    \"requestHooks\" : [ ]\n" +
                "  }\n" +
                "}";
    }

    @Test
    public void shouldGetStoredExecutor() throws MaestroCheckedException {
        final Executor testObject = getTestObject();
        HashMap<String, Executor> actual = FederatedUtil.getFederatedExecutors(testObject);

        final HashMap<String, Executor> expected = new HashMap<>();
        expected.put("Executor1", new Executor().config(new Config().id("ExecutorId1")));
        expected.put("Executor2", new Executor().config(new Config().id("ExecutorId2")));

        assertEquals(expected, actual);
    }


    @Override
    protected Executor getTestObject() {
        final Properties properties = new Properties();
        try {
            properties.put("ExecutorStore_Executor1", new String(JSONSerialiser.serialise(new Executor().config(new Config().id("ExecutorId1")), false)));
            properties.put("ExecutorStore_Executor2", new String(JSONSerialiser.serialise(new Executor().config(new Config().id("ExecutorId2")), false)));
        } catch (SerialisationException e) {
            e.printStackTrace();
        }

        return new Executor().config(new Config().setProperties(properties));
    }
}
