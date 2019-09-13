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

import org.junit.Before;
import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.handler.AddExecutorHandler;
import uk.gov.gchq.maestro.federated.handler.RemoveExecutorHandler;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.user.User;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RemoveExecutorsFederatedUtilTest {


    private Executor executor;
    private Executor subExecutor;

    @Before
    public void setUp() throws Exception {
        executor = new Executor(new Config());
        subExecutor = new Executor(new Config("sub"));
        AddExecutorsFederatedUtil.addExecutorTo(executor, new Operation("addOp").operationArg(AddExecutorHandler.EXECUTOR, subExecutor), new Context());
    }

    @Test
    public void shouldSetUpCorrectly() throws MaestroCheckedException {
        //Then
        final Collection<Executor> allExecutorsFrom = GetAllExecutorsFederatedUtil.getAllExecutorsFrom(executor, new User(User.UNKNOWN_USER_ID));
        assertEquals(1, allExecutorsFrom.size());
        assertTrue(allExecutorsFrom.contains(subExecutor));
    }

    @Test
    public void removeExecutorsFrom() throws MaestroCheckedException {
        //When
        RemoveExecutorsFederatedUtil.removeExecutorsFrom(executor,
                new Operation("TestOp")
                        .operationArg(RemoveExecutorHandler.EXECUTOR_ID, "sub"),
                new User(User.UNKNOWN_USER_ID));

        //Then
        final Collection<Executor> allExecutorsFrom = GetAllExecutorsFederatedUtil.getAllExecutorsFrom(executor, new User(User.UNKNOWN_USER_ID));
        assertEquals(0, allExecutorsFrom.size());
        assertFalse(allExecutorsFrom.contains(subExecutor));
    }

    @Test
    public void shouldErrorOperationIsNull() throws MaestroCheckedException {
        try {
            RemoveExecutorsFederatedUtil.removeExecutorsFrom(null, (Operation) null, null);
            fail("exception expected");
        } catch (Exception e) {
            assertEquals("Error removing executors due to -> operation is null", e.getMessage());
        }
    }

    @Test
    public void shouldErrorExecutorIsNull() throws MaestroCheckedException {
        final Operation testOp = new Operation("testOp");
        try {
            RemoveExecutorsFederatedUtil.removeExecutorsFrom(null, testOp, null);
            fail("exception expected");
        } catch (Exception e) {
            assertEquals("Error removing executorsids: null due to -> Executor is null", e.getMessage());
        }
    }
}
