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

import com.google.common.collect.Lists;
import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.federatedexecutor.FederatedAccess;
import uk.gov.gchq.maestro.federatedexecutor.FederatedExecutorStorage;
import uk.gov.gchq.maestro.helper.MaestroObjectTest;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Config;
import uk.gov.gchq.maestro.util.FederatedHandlersUtil;
import uk.gov.gchq.maestro.util.FederatedPropertiesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class FederatedExecutorTest extends MaestroObjectTest<Executor> {

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
                "      \"ExecutorStorage\" : \"{\\\"class\\\":\\\"uk.gov.gchq.maestro.federatedexecutor.FederatedExecutorStorage\\\",\\\"storage\\\":{\\\"{\\\\\\\"class\\\\\\\":\\\\\\\"uk.gov.gchq.maestro.federatedexecutor.FederatedAccess\\\\\\\",\\\\\\\"addingUserId\\\\\\\":\\\\\\\"user1\\\\\\\",\\\\\\\"graphAuths\\\\\\\":[],\\\\\\\"disabledByDefault\\\\\\\":false,\\\\\\\"public\\\\\\\":false}\\\":[\\\"java.util.HashSet\\\",[{\\\"class\\\":\\\"uk.gov.gchq.maestro.Executor\\\",\\\"config\\\":{\\\"class\\\":\\\"uk.gov.gchq.maestro.util.Config\\\",\\\"id\\\":\\\"ExecutorId2\\\",\\\"operationHandlers\\\":{},\\\"properties\\\":{},\\\"operationHooks\\\":[],\\\"requestHooks\\\":[]}},{\\\"class\\\":\\\"uk.gov.gchq.maestro.Executor\\\",\\\"config\\\":{\\\"class\\\":\\\"uk.gov.gchq.maestro.util.Config\\\",\\\"id\\\":\\\"ExecutorId1\\\",\\\"operationHandlers\\\":{},\\\"properties\\\":{},\\\"operationHooks\\\":[],\\\"requestHooks\\\":[]}}]]}}\"\n" +
                "    },\n" +
                "    \"operationHooks\" : [ ],\n" +
                "    \"requestHooks\" : [ ]\n" +
                "  }\n" +
                "}";
    }

    @Test
    public void shouldGetStoredExecutor() throws Exception {
        final Executor testObject = getTestObject();
        List<Executor> actual = FederatedHandlersUtil.getExecutorsFrom(testObject, new User("user1"), Lists.newArrayList("ExecutorId1", "ExecutorId2"));

        final List<Executor> expected = new ArrayList<>();
        expected.add(new Executor().config(new Config().id("ExecutorId1")));
        expected.add(new Executor().config(new Config().id("ExecutorId2")));

        assertEquals(expected, actual);
    }


    @Override
    protected Executor getTestObject() throws Exception {
        final Properties properties = new Properties();
        final Executor executor1 = new Executor().config(new Config().id("ExecutorId1"));
        final Executor executor2 = new Executor().config(new Config().id("ExecutorId2"));
        final FederatedExecutorStorage federatedExecutorStorage = new FederatedExecutorStorage();
        federatedExecutorStorage.put(executor1, new FederatedAccess(null, "user1"));
        federatedExecutorStorage.put(executor2, new FederatedAccess(null, "user1"));
        properties.put(FederatedPropertiesUtil.EXECUTOR_STORAGE, new String(JSONSerialiser.serialise(federatedExecutorStorage)));

        return new Executor().config(new Config().setProperties(properties));
    }
}
