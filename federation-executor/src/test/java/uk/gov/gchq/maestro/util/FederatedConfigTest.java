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

import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.federatedexecutor.operation.FederatedAccess;
import uk.gov.gchq.maestro.federatedexecutor.operation.FederatedExecutorStorage;
import uk.gov.gchq.maestro.helper.MaestroObjectTest;

import java.util.Properties;

import static org.junit.Assert.*;

public class FederatedConfigTest extends ConfigTest {

    @Override
    protected String getJSONString() {
        return "";
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