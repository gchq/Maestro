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
import uk.gov.gchq.maestro.federatedexecutor.FederatedAccess;
import uk.gov.gchq.maestro.federatedexecutor.FederatedExecutorStorage;
import uk.gov.gchq.maestro.federatedexecutor.operation.handler.impl.AddExecutorHandlerBasicTest;
import uk.gov.gchq.maestro.helper.MaestroObjectTest;
import uk.gov.gchq.maestro.util.Config;

import java.util.Set;


public class FederatedExecutorStorageTest extends MaestroObjectTest<FederatedExecutorStorage> {

    @Override
    protected Class getTestObjectClass() {
        return FederatedExecutorStorage.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.federatedexecutor.FederatedExecutorStorage\",\n" +
                "  \"storage\" : {\n" +
                "    \"{\\\"class\\\":\\\"uk.gov.gchq.maestro.federatedexecutor.FederatedAccess\\\",\\\"addingUserId\\\":\\\"testUser1\\\",\\\"graphAuths\\\":[\\\"one\\\"],\\\"disabledByDefault\\\":false,\\\"public\\\":false}\" : [ \"java.util.HashSet\", [ {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.Executor\",\n" +
                "      \"config\" : {\n" +
                "        \"class\" : \"uk.gov.gchq.maestro.util.Config\",\n" +
                "        \"id\" : \"innerExecutorId1\",\n" +
                "        \"operationHandlers\" : { },\n" +
                "        \"properties\" : { },\n" +
                "        \"operationHooks\" : [ ],\n" +
                "        \"requestHooks\" : [ ]\n" +
                "      }\n" +
                "    } ] ],\n" +
                "    \"{\\\"class\\\":\\\"uk.gov.gchq.maestro.federatedexecutor.FederatedAccess\\\",\\\"addingUserId\\\":\\\"testUser2\\\",\\\"graphAuths\\\":[\\\"one\\\"],\\\"disabledByDefault\\\":false,\\\"public\\\":false}\" : [ \"java.util.HashSet\", [ {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.Executor\",\n" +
                "      \"config\" : {\n" +
                "        \"class\" : \"uk.gov.gchq.maestro.util.Config\",\n" +
                "        \"id\" : \"innerExecutorId2\",\n" +
                "        \"operationHandlers\" : { },\n" +
                "        \"properties\" : { },\n" +
                "        \"operationHooks\" : [ ],\n" +
                "        \"requestHooks\" : [ ]\n" +
                "      }\n" +
                "    } ] ]\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected FederatedExecutorStorage getTestObject() throws Exception {
        final FederatedExecutorStorage federatedExecutorStorage = new FederatedExecutorStorage();
        final FederatedAccess access1 = new FederatedAccess((Set<String>) Sets.newHashSet("one"), "testUser1", false);
        federatedExecutorStorage.put(new Executor().config(new Config().id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + 1)), access1);

        final FederatedAccess access2 = new FederatedAccess((Set<String>) Sets.newHashSet("one"), "testUser2", false);
        federatedExecutorStorage.put(new Executor().config(new Config().id(AddExecutorHandlerBasicTest.INNER_EXECUTOR_ID + 2)), access2);

        return federatedExecutorStorage;
    }
}
