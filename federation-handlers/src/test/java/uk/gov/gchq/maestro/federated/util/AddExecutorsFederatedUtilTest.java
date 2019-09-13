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

import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.handler.AddExecutorHandler;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.user.User;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AddExecutorsFederatedUtilTest {
    @Test
    public void shouldErrorOpIsNull() throws Exception {
        try {
            AddExecutorsFederatedUtil.addExecutorTo(null, null, null);
            fail("exception expected");
        } catch (Exception e) {
            assertEquals("Error adding executor due to -> operation is null", e.getMessage());
        }
    }

    @Test
    public void shouldErrorContextIsNull() throws Exception {

        final Operation testOp = new Operation("testOp");

        try {
            AddExecutorsFederatedUtil.addExecutorTo(null, testOp, null);
            fail("exception expected");
        } catch (Exception e) {
            assertEquals("Error adding executor due to -> context is null", e.getMessage());
        }
    }

    @Test
    public void shouldErrorExecutorIsNull() throws Exception {
        final Operation testOp = new Operation("testOp");
        final Context context = new Context();

        try {
            AddExecutorsFederatedUtil.addExecutorTo(null, testOp, context);
            fail("exception expected");
        } catch (Exception e) {
            assertEquals("Error adding executor due to -> executor is null", e.getMessage());
        }
    }

    @Test
    public void shouldErrorAddingExecutorIsNull() throws Exception {
        final Operation testOp = new Operation("testOp");
        final Context context = new Context();

        try {
            AddExecutorsFederatedUtil.addExecutorTo(new Executor(new Config()), testOp, context);
            fail("exception expected");
        } catch (Exception e) {
            assertEquals("Error adding executor due to -> Error adding executor to federatedExecutorStorage due to -> The executor to be added to the FederatedExecutorStorage cannot be null", e.getMessage());
        }

        testOp.operationArg(AddExecutorHandler.EXECUTOR, new Executor(new Config("subExecutorTest")));
        AddExecutorsFederatedUtil.addExecutorTo(new Executor(new Config()), testOp, context);
    }

    @Test
    public void shouldErrorUserNameIsNull() throws Exception {
        final Operation testOp = new Operation("testOp");
        final Context context = new Context();

        try {
            AddExecutorsFederatedUtil.addExecutorTo(new Executor(new Config()), null, null, false, false, new Executor(new Config("innerExecutor")));
            fail("exception expected");
        } catch (Exception e) {
            assertEquals("Error adding executor due to -> userId is null", e.getMessage());
        }

        testOp.operationArg(AddExecutorHandler.EXECUTOR, new Executor(new Config("subExecutorTest")));
        AddExecutorsFederatedUtil.addExecutorTo(new Executor(new Config()), testOp, context);
    }

    @Test
    public void shouldAddExecutor() throws MaestroCheckedException {
        final Executor expectedInner = new Executor(new Config("innerExecutor"));
        final Operation testOp = new Operation("testOp")
                .operationArg(AddExecutorHandler.EXECUTOR, expectedInner);
        final Context context = new Context();

        final Executor receivingExecutor = new Executor(new Config());
        AddExecutorsFederatedUtil.addExecutorTo(receivingExecutor, testOp, context);

        final Collection<Executor> allExecutorsFrom = GetAllExecutorsFederatedUtil.getAllExecutorsFrom(receivingExecutor, new User());
        assertEquals(1, allExecutorsFrom.size());
        assertTrue(allExecutorsFrom.contains(expectedInner));
    }

    @Test
    public void shouldAddExecutorAlt() throws MaestroCheckedException {
        final Executor expectedInner = new Executor(new Config("innerExecutor"));

        final Executor receivingExecutor = new Executor(new Config());
        AddExecutorsFederatedUtil.addExecutorTo(receivingExecutor, new User().getUserId(), null, false, false, expectedInner);

        final Collection<Executor> allExecutorsFrom = GetAllExecutorsFederatedUtil.getAllExecutorsFrom(receivingExecutor, new User());
        assertEquals(1, allExecutorsFrom.size());
        assertTrue(allExecutorsFrom.contains(expectedInner));
    }
}
