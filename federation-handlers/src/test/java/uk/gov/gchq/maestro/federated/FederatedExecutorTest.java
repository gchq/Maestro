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
package uk.gov.gchq.maestro.federated;

import com.google.common.collect.Lists;
import org.junit.Test;

import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.util.ExecutorStorageFederatedUtil;
import uk.gov.gchq.maestro.federated.util.GetExecutorsFederatedUtil;
import uk.gov.gchq.maestro.operation.helper.MaestroObjectTest;
import uk.gov.gchq.maestro.operation.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FederatedExecutorTest extends MaestroObjectTest<Executor> {

    @Override
    protected Class getTestObjectClass() {
        return Executor.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.executor.Executor\",\n" +
                "  \"config\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.executor.util.Config\",\n" +
                "    \"operationHandlers\" : { },\n" +
                "    \"properties\" : {\n" +
                "      \"executorStorage\" : {\n" +
                "        \"class\" : \"uk.gov.gchq.maestro.federated.FederatedExecutorStorage\",\n" +
                "        \"storage\" : {\n" +
                "          \"{\\\"class\\\":\\\"uk.gov.gchq.maestro.federated.FederatedAccess\\\",\\\"addingUserId\\\":\\\"user1\\\",\\\"auths\\\":[],\\\"disabledByDefault\\\":false,\\\"public\\\":false}\" : [ \"java.util.TreeSet\", [ {\n" +
                "            \"class\" : \"uk.gov.gchq.maestro.executor.Executor\",\n" +
                "            \"config\" : {\n" +
                "              \"class\" : \"uk.gov.gchq.maestro.executor.util.Config\",\n" +
                "              \"id\" : \"ExecutorId1\",\n" +
                "              \"operationHandlers\" : { },\n" +
                "              \"properties\" : { },\n" +
                "              \"defaultHandler\" : {\n" +
                "                \"class\" : \"uk.gov.gchq.maestro.executor.operation.handler.DefaultHandler\"\n" +
                "              },\n" +
                "              \"operationHooks\" : [ ],\n" +
                "              \"requestHooks\" : [ ]\n" +
                "            }\n" +
                "          }, {\n" +
                "            \"class\" : \"uk.gov.gchq.maestro.executor.Executor\",\n" +
                "            \"config\" : {\n" +
                "              \"class\" : \"uk.gov.gchq.maestro.executor.util.Config\",\n" +
                "              \"id\" : \"ExecutorId2\",\n" +
                "              \"operationHandlers\" : { },\n" +
                "              \"properties\" : { },\n" +
                "              \"defaultHandler\" : {\n" +
                "                \"class\" : \"uk.gov.gchq.maestro.executor.operation.handler.DefaultHandler\"\n" +
                "              },\n" +
                "              \"operationHooks\" : [ ],\n" +
                "              \"requestHooks\" : [ ]\n" +
                "            }\n" +
                "          } ] ]\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"defaultHandler\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.executor.operation.handler.DefaultHandler\"\n" +
                "    },\n" +
                "    \"operationHooks\" : [ ],\n" +
                "    \"requestHooks\" : [ ]\n" +
                "  }\n" +
                "}";
    }

    @Test
    public void shouldGetStoredExecutor() throws Exception {
        final Executor testObject = getFullyPopulatedTestObject();
        Collection<Executor> actual = GetExecutorsFederatedUtil.getExecutorsFrom(testObject, new User("user1"), Lists.newArrayList("ExecutorId1", "ExecutorId2"));

        final Collection<Executor> expected = new ArrayList<>();
        expected.add(new Executor(new Config().id("ExecutorId1")));
        expected.add(new Executor(new Config().id("ExecutorId2")));

        assertEquals(expected.size(), actual.size());
        assertTrue(Arrays.equals(expected.toArray(), actual.toArray()));

    }

    @Override
    protected Executor getFullyPopulatedTestObject() throws Exception {
        final Map<String, Object> properties = new HashMap<>();
        final Executor executor1 = new Executor(new Config().id("ExecutorId1"));
        final Executor executor2 = new Executor(new Config().id("ExecutorId2"));
        final FederatedExecutorStorage federatedExecutorStorage = new FederatedExecutorStorage();
        federatedExecutorStorage.put(executor1, new FederatedAccess(null, "user1"));
        federatedExecutorStorage.put(executor2, new FederatedAccess(null, "user1"));
        properties.put(ExecutorStorageFederatedUtil.EXECUTOR_STORAGE, federatedExecutorStorage);

        return new Executor(new Config().setProperties(properties));
    }
}
