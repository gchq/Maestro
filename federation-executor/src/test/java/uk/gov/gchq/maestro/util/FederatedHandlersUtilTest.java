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

import com.google.common.collect.Lists;
import org.junit.Test;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.federatedexecutor.FederatedAccess;
import uk.gov.gchq.maestro.federatedexecutor.FederatedExecutorStorage;
import uk.gov.gchq.maestro.user.User;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FederatedHandlersUtilTest {
    @Test
    public void shouldGetExecutor() throws MaestroCheckedException {
        //given
        final Executor expected = new Executor().config(new Config().id("ExecutorId1"));
        final Properties properties = new Properties();
        final FederatedExecutorStorage storage = new FederatedExecutorStorage().put(expected, new FederatedAccess(null, "tempUser", false));
        FederatedPropertiesUtil.putSerialisedExecutorStorage(properties, storage);

        //when
        final List<Executor> executors = FederatedHandlersUtil.getExecutorsFrom(properties, new User("tempUser"), Lists.newArrayList("ExecutorId1"));

        //then
        assertEquals(1, executors.size());
        assertEquals(expected, executors.get(0));
    }

    @Test
    public void shouldNotGetExecutorWithNull() throws MaestroCheckedException {
        //given
        final Executor expected = new Executor().config(new Config().id("ExecutorId1"));
        final Properties properties = new Properties();
        final FederatedExecutorStorage storage = new FederatedExecutorStorage().put(expected, new FederatedAccess(null, "tempUser", false));
        FederatedPropertiesUtil.putSerialisedExecutorStorage(properties, storage);

        //when
        try {
            final List<Executor> executors = FederatedHandlersUtil.getExecutorsFrom(properties, new User("tempUser"), null);
            fail("exception expected");
        } catch (Exception e) {
            assertEquals("Can't get Executors with null ids", e.getMessage());
        }
    }

    @Test
    public void shouldErrorWithWrongValueType() {
        //given
        final Integer value = 1;
        final Properties properties = new Properties();
        properties.put(FederatedPropertiesUtil.EXECUTOR_STORAGE, value);

        try {
            //when
            FederatedHandlersUtil.getExecutorsFrom(properties, null, null);
            fail("exception expected");
        } catch (MaestroCheckedException e) {
            //then
            assertTrue(e.getMessage().contains(String.format(FederatedPropertiesUtil.VALUE_FOR_PROPERTY_KEY_S_EXPECTED_CLASS_S_FOUND_S, FederatedPropertiesUtil.EXECUTOR_STORAGE, String.class.getCanonicalName(), value.getClass().getCanonicalName())));
        }
    }
}
