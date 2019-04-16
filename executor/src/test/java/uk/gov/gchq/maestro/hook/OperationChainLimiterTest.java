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
package uk.gov.gchq.maestro.hook;

import org.junit.Test;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.commonutil.exception.UnauthorisedException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.impl.output.ToArray;
import uk.gov.gchq.maestro.operation.impl.output.ToCsv;
import uk.gov.gchq.maestro.operation.impl.output.ToList;
import uk.gov.gchq.maestro.operation.impl.output.ToSet;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Request;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class OperationChainLimiterTest extends HookTest<OperationChainLimiter> {
    private static final String OP_CHAIN_LIMITER_PATH = "opChainLimiter.json";

    public OperationChainLimiterTest() {
        super(OperationChainLimiter.class);
    }

    @Test
    public void shouldAcceptOperationChainWhenUserHasAuthScoreGreaterThanChainScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder()
                .first(new ToList<>())
                .build();
        final User user = new User.Builder()
                .opAuths("User")
                .build();

        // When
        hook.preExecute(new Request(opChain, new Context(user)));

        // Then - no exceptions
    }

    @Test
    public void shouldAcceptOperationChainWhenUserHasAuthScoreEqualToChainScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder()
                .first(new ToCsv<>())
                .build();
        final User user = new User.Builder()
                .opAuths("User")
                .build();

        // When
        hook.preExecute(new Request(opChain, new Context(user)));

        // Then - no exceptions
    }

    @Test
    public void shouldRejectOperationChainWhenUserHasAuthScoreLessThanChainScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder()
                .first(new ToArray<>())
                .then(new ToArray())
                .then(new ToArray<>())
                .then(new ToSet<>())
                .then(new ToList<>())
                .build();
        final User user = new User.Builder()
                .opAuths("User")
                .build();

        // When/Then

        try {
            hook.preExecute(new Request(opChain, new Context(user)));
            fail("Exception expected");
        } catch (final UnauthorisedException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldAcceptOperationChainWhenUserHasMaxAuthScoreGreaterThanChainScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder()
                .first(new ToCsv<>())
                .then(new ToList<>())
                .build();
        final User user = new User.Builder()
                .opAuths("SuperUser", "User")
                .build();

        // When
        hook.preExecute(new Request(opChain, new Context(user)));

        // Then - no exceptions
    }

    @Test
    public void shouldRejectOperationChainWhenUserHasMaxAuthScoreLessThanChainScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder()
                .first(new ToArray<>())
                .then(new ToSet())
                .then(new ToList<>())
                .build();
        final User user = new User.Builder()
                .opAuths("SuperUser", "User")
                .build();

        // When/Then
        try {
            hook.preExecute(new Request(opChain, new Context(user)));
            fail("Exception expected");
        } catch (final UnauthorisedException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldRejectOperationChainWhenUserHasNoAuthWithAConfiguredScore() {
        // Given
        final OperationChainLimiter hook = fromJson(OP_CHAIN_LIMITER_PATH);
        final OperationChain opChain = new OperationChain.Builder()
                .first(new ToArray<>())
                .build();
        final User user = new User.Builder()
                .opAuths("NoScore")
                .build();

        // When/Then
        try {
            hook.preExecute(new Request(opChain, new Context(user)));
            fail("Exception expected");
        } catch (final UnauthorisedException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldReturnResultWithoutModification() {
        // Given
        final OperationChainLimiter hook = fromJson(OP_CHAIN_LIMITER_PATH);
        final Object result = mock(Object.class);
        final OperationChain opChain = new OperationChain.Builder()
                .first(new ToSet<>())
                .build();
        final User user = new User.Builder()
                .opAuths("NoScore")
                .build();

        // When
        final Object returnedResult = hook.postExecute(result,
                new Request(opChain, new Context(user)));

        // Then
        assertSame(result, returnedResult);
    }

    @Override
    protected OperationChainLimiter getTestObject() {
        return fromJson(OP_CHAIN_LIMITER_PATH);
    }

    @Test
    public void shouldSetAndGetAuthScores() {
        // Given
        final OperationChainLimiter hook = new OperationChainLimiter();
        final Map<String, Integer> authScores = new HashMap<>();
        authScores.put("auth1", 1);
        authScores.put("auth2", 2);
        authScores.put("auth3", 3);

        // When
        hook.setAuthScores(authScores);
        final Map<String, Integer> result = hook.getAuthScores();

        // Then
        assertEquals(authScores, result);
    }

    @Test
    public void shouldSetAndGetOpScores() {
        // Given
        final OperationChainLimiter hook = new OperationChainLimiter();
        final LinkedHashMap<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(ToArray.class, 2);
        opScores.put(ToList.class, 3);

        // When
        hook.setOpScores(opScores);
        final Map<Class<? extends Operation>, Integer> result = hook.getOpScores();

        // Then
        assertEquals(opScores, result);
    }

    @Test
    public void shouldSetAndGetOpScoresAsStrings() throws ClassNotFoundException {
        // Given
        final OperationChainLimiter hook = new OperationChainLimiter();
        final LinkedHashMap<String, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class.getName(), 1);
        opScores.put(ToArray.class.getName(), 2);
        opScores.put(ToSet.class.getName(), 3);

        // When
        hook.setOpScoresFromStrings(opScores);
        final Map<String, Integer> result = hook.getOpScoresAsStrings();

        // Then
        assertEquals(opScores, result);
    }
}
