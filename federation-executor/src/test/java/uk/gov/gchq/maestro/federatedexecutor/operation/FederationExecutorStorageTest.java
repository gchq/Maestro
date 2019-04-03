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

import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.helper.MaestroObjectTest;
import uk.gov.gchq.maestro.util.Config;

import java.util.Properties;

import static uk.gov.gchq.maestro.util.FederatedUtil.EXECUTOR_STORE;


public class FederationExecutorStorageTest extends MaestroObjectTest<Executor> {

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
                "      \"ExecutorStore_executor1\" : \"{\\\"class\\\":\\\"uk.gov.gchq.maestro.Executor\\\",\\\"config\\\":{\\\"class\\\":\\\"uk.gov.gchq.maestro.util.Config\\\",\\\"operationHandlers\\\":{},\\\"properties\\\":{\\\"innerK1\\\":\\\"innerV1\\\"},\\\"operationHooks\\\":[],\\\"requestHooks\\\":[]}}\",\n" +
                "      \"ExecutorStore_executor2\" : \"{\\\"class\\\":\\\"uk.gov.gchq.maestro.Executor\\\",\\\"config\\\":{\\\"class\\\":\\\"uk.gov.gchq.maestro.util.Config\\\",\\\"operationHandlers\\\":{},\\\"properties\\\":{\\\"innerK2\\\":\\\"innerV2\\\"},\\\"operationHooks\\\":[],\\\"requestHooks\\\":[]}}\",\n" +
                "      \"values\" : \"[\\\"val2\\\",\\\"val1\\\"]\"\n" +
                "    },\n" +
                "    \"operationHooks\" : [ ],\n" +
                "    \"requestHooks\" : [ ]\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Executor getTestObject() {
        final Properties properties = new Properties();

        try {
            properties.put("values", new String(JSONSerialiser.serialise(Sets.newHashSet("val1", "val2"), false)));
        } catch (SerialisationException e) {
            e.printStackTrace();
        }


        try {
            final Properties properties1 = new Properties();
            properties1.put("innerK1", "innerV1");
            properties.put(EXECUTOR_STORE + "executor1", new String(JSONSerialiser.serialise(new Executor().config(new Config().setProperties(properties1)), false)));

            final Properties properties2 = new Properties();
            properties2.put("innerK2", "innerV2");
            properties.put(EXECUTOR_STORE + "executor2", new String(JSONSerialiser.serialise(new Executor().config(new Config().setProperties(properties2)), false)));
        } catch (SerialisationException e) {
            e.printStackTrace();
        }


        final Executor executor = new Executor()
                .config(new Config().setProperties(properties));
        return executor;
    }
}
