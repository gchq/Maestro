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

package uk.gov.gchq.maestro.federated.util;

import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.federated.FederatedAccess;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;

import java.util.Properties;

import static org.junit.Assert.assertNull;

public class FederatedConfigTest extends ConfigTest {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"id\" : \"configIdValue\",\n" +
                "  \"operationHandlers\" : {\n" +
                "    \"uk.gov.gchq.maestro.helper.TestOperation\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.helper.TestHandler\",\n" +
                "      \"handlerField\" : \"handlerFieldValue1\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"properties\" : {\n" +
                "    \"configKey\" : \"configValue\"\n" +
                "  },\n" +
                "  \"operationHooks\" : [ ],\n" +
                "  \"requestHooks\" : [ ]\n" +
                "}";
    }

    @Override
    protected Config getTestObject() throws Exception {

        final Config testObject = super.getTestObject();
        final Properties properties = new Properties();
        final Object o = properties.get(FederatedPropertiesUtil.EXECUTOR_STORAGE);
        assertNull("parent class should not be dealing with property calues of federated " + FederatedPropertiesUtil.EXECUTOR_STORAGE, o);
        final FederatedExecutorStorage federatedExecutorStorage = new FederatedExecutorStorage();
        federatedExecutorStorage.put(new Executor().config(new Config().id("inner")), new FederatedAccess(Sets.newHashSet("valueA"), "addingUser"));

        properties.put(FederatedPropertiesUtil.EXECUTOR_STORAGE, federatedExecutorStorage);
        return testObject;
    }
}
