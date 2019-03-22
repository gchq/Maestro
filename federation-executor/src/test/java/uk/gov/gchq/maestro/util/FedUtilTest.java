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

import org.junit.Test;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;

import java.util.HashMap;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FedUtilTest {
    @Test
    public void shouldGetExecutor() throws MaestroCheckedException {
        //given
        final Executor expected = new Executor().config(new Config().setId("ExecutorId1"));
        final Properties properties = new Properties();
        properties.put("ExecutorStore_1", "{\"class\":\"uk.gov.gchq.maestro.Executor\",\"config\":{\"class\":\"uk.gov.gchq.maestro.util.Config\",\"id\":\"ExecutorId1\",\"operationHandlers\":{},\"hooks\":[]}}\"");

        //when
        final HashMap<String, Executor> executors = FedUtil.getFederatedExecutors(properties);

        //then
        assertEquals(1, executors.size());
        assertEquals(expected, executors.get("ExecutorStore_1"));
    }

    @Test
    public void shouldErrorWithFailedDeserialising() {
        //given
        final String key = "ExecutorStore_1";
        final String value = "";
        final Properties properties = new Properties();
        properties.put(key, value);

        try {
            //when
            FedUtil.getFederatedExecutors(properties);
            fail("exception expected");
        } catch (MaestroCheckedException e) {
            //then
            assertEquals(String.format(FedUtil.ERROR_DESERIALISING_EXECUTOR_FROM_PROPERTY_VALUE_STRING, key, value), e.getMessage());
        }
    }

    @Test
    public void shouldErrorWithWrongValueType() {
        //given
        final String key = "ExecutorStore_1";
        final Integer value = 1;
        final Properties properties = new Properties();
        properties.put(key, value);

        try {
            //when
            FedUtil.getFederatedExecutors(properties);
            fail("exception expected");
        } catch (MaestroCheckedException e) {
            //then
            assertEquals(String.format(FedUtil.VALUE_FOR_PROPERTY_S_EXPECTED_STRING_FOUND_S, key, value.getClass()), e.getMessage());
        }
    }
}