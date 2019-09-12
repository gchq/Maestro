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
package uk.gov.gchq.maestro.federated.operation;

import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.FederatedAccess;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.federated.operation.handler.AddExecutorHandlerBasicTest;
import uk.gov.gchq.maestro.operation.helper.MaestroObjectTest;

import java.util.HashSet;


public class FederatedExecutorStorageTest extends MaestroObjectTest<FederatedExecutorStorage> {

    @Override
    protected Class getTestObjectClass() {
        return FederatedExecutorStorage.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.federated.FederatedExecutorStorage\",\n" +
                "  \"storage\" : {\n" +
                "    \"{\\\"class\\\":\\\"uk.gov.gchq.maestro.federated.FederatedAccess\\\",\\\"addingUserId\\\":\\\"testUser1\\\",\\\"auths\\\":[\\\"one\\\"],\\\"disabledByDefault\\\":false,\\\"public\\\":false}\" : [ \"java.util.TreeSet\", [ {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.executor.Executor\",\n" +
                "      \"config\" : {\n" +
                "        \"class\" : \"uk.gov.gchq.maestro.executor.util.Config\",\n" +
                "        \"id\" : \"innerExecutorId1\",\n" +
                "        \"operationHandlers\" : { },\n" +
                "        \"properties\" : { },\n" +
                "        \"defaultHandler\" : {\n" +
                "          \"class\" : \"uk.gov.gchq.maestro.executor.operation.handler.DefaultHandler\"\n" +
                "        },\n" +
                "        \"operationHooks\" : [ ],\n" +
                "        \"requestHooks\" : [ ]\n" +
                "      }\n" +
                "    } ] ],\n" +
                "    \"{\\\"class\\\":\\\"uk.gov.gchq.maestro.federated.FederatedAccess\\\",\\\"addingUserId\\\":\\\"testUser2\\\",\\\"auths\\\":[\\\"one\\\"],\\\"disabledByDefault\\\":false,\\\"public\\\":false}\" : [ \"java.util.TreeSet\", [ {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.executor.Executor\",\n" +
                "      \"config\" : {\n" +
                "        \"class\" : \"uk.gov.gchq.maestro.executor.util.Config\",\n" +
                "        \"id\" : \"innerExecutorId2\",\n" +
                "        \"operationHandlers\" : { },\n" +
                "        \"properties\" : { },\n" +
                "        \"defaultHandler\" : {\n" +
                "          \"class\" : \"uk.gov.gchq.maestro.executor.operation.handler.DefaultHandler\"\n" +
                "        },\n" +
                "        \"operationHooks\" : [ ],\n" +
                "        \"requestHooks\" : [ ]\n" +
                "      }\n" +
                "    }, {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.executor.Executor\",\n" +
                "      \"config\" : {\n" +
                "        \"class\" : \"uk.gov.gchq.maestro.executor.util.Config\",\n" +
                "        \"id\" : \"innerExecutorId3\",\n" +
                "        \"operationHandlers\" : { },\n" +
                "        \"properties\" : { },\n" +
                "        \"defaultHandler\" : {\n" +
                "          \"class\" : \"uk.gov.gchq.maestro.executor.operation.handler.DefaultHandler\"\n" +
                "        },\n" +
                "        \"operationHooks\" : [ ],\n" +
                "        \"requestHooks\" : [ ]\n" +
                "      }\n" +
                "    } ] ]\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected FederatedExecutorStorage getFullyPopulatedTestObject() throws Exception {
        final FederatedExecutorStorage federatedExecutorStorage = new FederatedExecutorStorage();
        final HashSet<String> graphAuths = Sets.newHashSet("one");

        final FederatedAccess access1 = new FederatedAccess(graphAuths, "testUser1", false);
        final Executor executor1 = new Executor(new Config().id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + 1));
        federatedExecutorStorage.put(executor1, access1);

        final FederatedAccess access2 = new FederatedAccess(graphAuths, "testUser2", false);
        final Executor executor2 = new Executor(new Config().id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + 2));
        final Executor executor3 = new Executor(new Config().id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + 3));
        federatedExecutorStorage.put(executor2, access2);
        federatedExecutorStorage.put(executor3, access2);

        return federatedExecutorStorage;
    }
}
