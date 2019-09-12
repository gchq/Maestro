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

import com.google.common.collect.Lists;
import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.FederatedAccess;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.operation.user.User;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FederatedHandlersUtilTest {
    @Test
    public void shouldGetExecutor() throws MaestroCheckedException {
        //given
        final Executor expectedConfig = new Executor(new Config("ExecutorId1"));
        final Executor actualconfig = new Executor(new Config("actual"));
        final FederatedExecutorStorage storage = new FederatedExecutorStorage().put(expectedConfig, new FederatedAccess(null, "tempUser", false));
        ExecutorStorageFederatedUtil.setExecutorStorage(actualconfig, storage);

        //when
        final Collection<Executor> executors = GetExecutorsFederatedUtil.getExecutorsFrom(actualconfig, new User("tempUser"), Lists.newArrayList("ExecutorId1"));

        //then
        assertEquals(1, executors.size());
        assertEquals(expectedConfig, executors.toArray()[0]);
    }

    @Test
    public void shouldNotGetExecutorWithNull() throws MaestroCheckedException {
        //given
        final Executor expectedExecutor = new Executor(new Config().id("ExecutorId1"));
        final Executor actualExecutor = new Executor(new Config("actual"));
        final FederatedExecutorStorage storage = new FederatedExecutorStorage().put(expectedExecutor, new FederatedAccess(null, "tempUser", false));
        ExecutorStorageFederatedUtil.setExecutorStorage(actualExecutor, storage);

        //when
        try {
            final Collection<Executor> executors = GetExecutorsFederatedUtil.getExecutorsFrom(actualExecutor, new User("tempUser"), (String) null);
            fail("exception expected");
        } catch (Exception e) {
            //    TODO improve
        }
    }
}
