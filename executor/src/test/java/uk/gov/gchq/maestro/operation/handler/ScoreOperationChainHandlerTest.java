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

package uk.gov.gchq.maestro.operation.handler;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.ExecutorProperties;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.named.operation.NamedOperation;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclarations;
import uk.gov.gchq.maestro.operation.impl.ScoreOperationChain;
import uk.gov.gchq.maestro.operation.impl.output.ToCsv;
import uk.gov.gchq.maestro.operation.impl.output.ToSet;
import uk.gov.gchq.maestro.operation.impl.output.ToSingletonList;
import uk.gov.gchq.maestro.operation.impl.output.ToStream;
import uk.gov.gchq.maestro.operation.resolver.DefaultScoreResolver;
import uk.gov.gchq.maestro.operation.resolver.ScoreResolver;
import uk.gov.gchq.maestro.operation.resolver.named.NamedOperationScoreResolver;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Config;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

public class ScoreOperationChainHandlerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldLoadFromScoreOperationChainDeclarationFile() throws SerialisationException {
        final InputStream s = StreamUtil.openStream(getClass(), "TestScoreOperationChainDeclaration.json");
        final OperationDeclarations deserialised = JSONSerialiser.deserialise(s, OperationDeclarations.class);

        assertEquals(1, deserialised.getOperations().size());
        assert (deserialised.getOperations().get(0).getHandler() instanceof ScoreOperationChainHandler);
    }

    @Test
    public void shouldExecuteScoreChainOperation() throws OperationException {
        // Given
        final ScoreOperationChainHandler operationHandler = new ScoreOperationChainHandler();

        final Context context = mock(Context.class);
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);
        final ScoreOperationChain scoreOperationChain = mock(ScoreOperationChain.class);

        ExecutorProperties executorProperties = new ExecutorProperties();

        final ToSet op1 = mock(ToSet.class);
        final ToCsv op2 = mock(ToCsv.class);
        final OperationChain opChain = new OperationChain(Arrays.asList(op1, op2));
        final Integer expectedResult = 2;

        given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        given(user.getOpAuths()).willReturn(opAuths);
        given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        given(executor.getConfig()).willReturn(new Config.Builder().addExecutorProperties(executorProperties).build());

        // When
        final Object result = operationHandler.doOperation(
                new ScoreOperationChain.Builder()
                        .operationChain(opChain)
                        .build(),
                context, executor);

        // Then
        assertSame(expectedResult, result);
    }

    @Test
    public void shouldExecuteScoreChainOperationForNestedOperationChain() throws OperationException {
        // Given
        final ScoreOperationChainHandler operationHandler = new ScoreOperationChainHandler();

        final Context context = mock(Context.class);
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);
        final ScoreOperationChain scoreOperationChain = mock(ScoreOperationChain.class);

        ExecutorProperties executorProperties = new ExecutorProperties();

        final ToSet op1 = mock(ToSet.class);
        final ToCsv op2 = mock(ToCsv.class);
        final ToSingletonList op3 = mock(ToSingletonList.class);
        final OperationChain opChain1 = new OperationChain(Arrays.asList(op1, op2));
        final OperationChain opChain = new OperationChain(Arrays.asList(opChain1, op3));
        final Integer expectedResult = 3;

        given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        given(user.getOpAuths()).willReturn(opAuths);
        given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        given(executor.getConfig()).willReturn(new Config.Builder().addExecutorProperties(executorProperties).build());

        // When
        final Object result = operationHandler.doOperation(
                new ScoreOperationChain.Builder()
                        .operationChain(opChain)
                        .build(),
                context, executor);

        // Then
        assertSame(expectedResult, result);
    }

    @Test
    public void shouldExecuteScoreOperationChainContainingNamedOperation() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();

        final ScoreResolver scoreResolver = mock(NamedOperationScoreResolver.class);

        final Context context = mock(Context.class);
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);
        final ScoreOperationChain scoreOperationChain = mock(ScoreOperationChain.class);

        final ToSet op1 = mock(ToSet.class);
        final ToCsv op2 = mock(ToCsv.class);
        final ToSingletonList op3 = mock(ToSingletonList.class);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(ToSet.class, 2);
        opScores.put(ToCsv.class, 1);
        opScores.put(ToSingletonList.class, 1);
        handler.setOpScores(opScores);

        final String opName = "basicOp";
        final NamedOperation namedOp = mock(NamedOperation.class);
        namedOp.setOperationName(opName);

        resolvers.put(namedOp.getClass(), scoreResolver);
        handler.setScoreResolvers(resolvers);

        given(scoreResolver.getScore(eq(namedOp), any())).willReturn(3);
        final OperationChain opChain = new OperationChain(Arrays.asList(op1, op2, op3, namedOp));

        given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        given(user.getOpAuths()).willReturn(opAuths);
        given(scoreOperationChain.getOperationChain()).willReturn(opChain);

        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder()
                .operationChain(opChain)
                .build(), context, executor);

        // Then
        assertEquals(7, result);
    }

    @Test
    public void shouldCorrectlyExecuteScoreOperationChainWhenNamedOperationScoreIsNull() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();

        final ScoreResolver scoreResolver = mock(NamedOperationScoreResolver.class);

        final Context context = mock(Context.class);
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);
        final ScoreOperationChain scoreOperationChain = mock(ScoreOperationChain.class);
        final ExecutorProperties executorProperties = new ExecutorProperties();

        final ToSet op1 = mock(ToSet.class);
        final ToCsv op2 = mock(ToCsv.class);
        final ToSingletonList op3 = mock(ToSingletonList.class);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(ToSet.class, 3);
        opScores.put(ToCsv.class, 2);
        opScores.put(ToSingletonList.class, 1);
        handler.setOpScores(opScores);

        final String opName = "basicOp";
        final NamedOperation namedOp = mock(NamedOperation.class);
        namedOp.setOperationName(opName);

        resolvers.put(namedOp.getClass(), scoreResolver);
        handler.setScoreResolvers(resolvers);

        given(scoreResolver.getScore(eq(namedOp), any())).willReturn(null);
        final OperationChain opChain = new OperationChain(Arrays.asList(op1, op2, op3, namedOp));

        given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        given(user.getOpAuths()).willReturn(opAuths);
        given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        given(executor.getConfig()).willReturn(new Config.Builder().addExecutorProperties(executorProperties).build());

        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder()
                .operationChain(opChain)
                .build(), context, executor);

        // Then
        assertEquals(7, result);

    }

    @Test
    public void shouldResolveScoreOperationChainWithMultipleScoreResolvers() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();

        final ScoreResolver scoreResolver = mock(NamedOperationScoreResolver.class);
        final ScoreResolver scoreResolver1 = mock(DefaultScoreResolver.class);

        final Context context = mock(Context.class);
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);
        final ScoreOperationChain scoreOperationChain = mock(ScoreOperationChain.class);

        final ToSet op1 = new ToSet();
        final ToSingletonList op2 = new ToSingletonList();
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(ToSet.class, 2);
        handler.setOpScores(opScores);

        final String opName = "namedOp";
        final NamedOperation namedOp = mock(NamedOperation.class);
        namedOp.setOperationName(opName);

        resolvers.put(namedOp.getClass(), scoreResolver);
        resolvers.put(op2.getClass(), scoreResolver1);
        handler.setScoreResolvers(resolvers);

        given(scoreResolver.getScore(eq(namedOp), any())).willReturn(3);
        given(scoreResolver1.getScore(eq(op2), any())).willReturn(5);

        final OperationChain opChain = new OperationChain(Arrays.asList(op1, op2, namedOp));

        given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        given(user.getOpAuths()).willReturn(opAuths);
        given(scoreOperationChain.getOperationChain()).willReturn(opChain);

        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder()
                .operationChain(opChain)
                .build(), context, executor);

        // Then
        assertEquals(10, result);
    }

    @Test
    public void shouldCorrectlyResolveScoreForNullListOfOperations() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();

        handler.setScoreResolvers(resolvers);

        final Context context = mock(Context.class);
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);
        final ScoreOperationChain scoreOperationChain = mock(ScoreOperationChain.class);

        final List<? extends Operation> opList = null;

        final OperationChain opChain = new OperationChain(opList);

        given(scoreOperationChain.getOperationChain()).willReturn(opChain);

        given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        given(user.getOpAuths()).willReturn(opAuths);

        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder()
                .operationChain(opChain)
                .build(), context, executor);

        // Then
        assertEquals(0, result);
    }

    @Test
    public void shouldCorrectlyResolveScoreForNestedOperationWithNullOperationList() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();

        final ScoreResolver scoreResolver = mock(NamedOperationScoreResolver.class);

        final Context context = mock(Context.class);
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);
        final ScoreOperationChain scoreOperationChain = mock(ScoreOperationChain.class);

        final ToStream op1 = mock(ToStream.class);
        final ToSingletonList op2 = mock(ToSingletonList.class);
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(ToStream.class, 2);
        opScores.put(ToSingletonList.class, 1);
        handler.setOpScores(opScores);

        final String opName = "namedOp";
        final NamedOperation namedOp = mock(NamedOperation.class);
        namedOp.setOperationName(opName);

        resolvers.put(namedOp.getClass(), scoreResolver);
        handler.setScoreResolvers(resolvers);

        given(scoreResolver.getScore(eq(namedOp), any())).willReturn(3);

        final List<? extends Operation> opList = null;

        final OperationChain nestedOpChain = new OperationChain(opList);

        final OperationChain opChain = new OperationChain(Arrays.asList(
                op1,
                op2,
                nestedOpChain,
                namedOp));

        given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        given(user.getOpAuths()).willReturn(opAuths);

        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder()
                .operationChain(opChain)
                .build(), context, executor);

        // Then
        assertEquals(6, result);
    }

    @Test
    public void shouldReturnZeroForANullOperationChain() throws OperationException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();

        final Context context = mock(Context.class);
        final Executor executor = mock(Executor.class);
        final User user = mock(User.class);
        final ScoreOperationChain scoreOperationChain = mock(ScoreOperationChain.class);

        final OperationChain opChain = null;

        given(scoreOperationChain.getOperationChain()).willReturn(opChain);
        given(context.getUser()).willReturn(user);
        Set<String> opAuths = new HashSet<>();
        opAuths.add("TEST_USER");
        given(user.getOpAuths()).willReturn(opAuths);

        // When
        final Object result = handler.doOperation(new ScoreOperationChain.Builder()
                .operationChain(opChain)
                .build(), context, executor);

        // Then
        assertEquals(0, result);
    }

    @Test
    public void shouldSetAndGetAuthScores() {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<String, Integer> authScores = new HashMap<>();
        authScores.put("auth1", 1);
        authScores.put("auth2", 2);
        authScores.put("auth3", 3);

        // When
        handler.setAuthScores(authScores);
        final Map<String, Integer> result = handler.getAuthScores();

        // Then
        assertEquals(authScores, result);
    }

    @Test
    public void shouldSetAndGetOpScores() {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final LinkedHashMap<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(ToCsv.class, 2);
        opScores.put(ToStream.class, 3);

        // When
        handler.setOpScores(opScores);
        final Map<Class<? extends Operation>, Integer> result = handler.getOpScores();

        // Then
        assertEquals(opScores, result);
    }

    @Test
    public void shouldSetAndGetOpScoresAsStrings() throws ClassNotFoundException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final LinkedHashMap<String, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class.getName(), 1);
        opScores.put(ToCsv.class.getName(), 2);
        opScores.put(ToStream.class.getName(), 3);

        // When
        handler.setOpScoresFromStrings(opScores);
        final Map<String, Integer> result = handler.getOpScoresAsStrings();

        // Then
        assertEquals(opScores, result);
    }

    @Test
    public void shouldPassValidationOfOperationScores() throws ClassNotFoundException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final LinkedHashMap<String, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class.getName(), 1);
        opScores.put(ToCsv.class.getName(), 2);
        opScores.put(ToStream.class.getName(), 3);

        // When
        handler.setOpScoresFromStrings(opScores);

        // Then - no exceptions
    }

    @Test
    public void shouldFailValidationOfOperationScores() throws ClassNotFoundException {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final LinkedHashMap<String, Integer> opScores = new LinkedHashMap<>();
        opScores.put(ToCsv.class.getName(), 2);
        opScores.put(ToStream.class.getName(), 3);
        opScores.put(Operation.class.getName(), 1);

        // When / Then
        try {
            handler.setOpScoresFromStrings(opScores);
            fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Operation scores are configured incorrectly."));
        }
    }

    @Test
    public void shouldAddDefaultScoreResolvers() {
        // Given
        final Map<Class<? extends Operation>, ScoreResolver> defaultResolvers = ScoreOperationChainHandler.getDefaultScoreResolvers();

        // When / Then
        assertTrue(defaultResolvers.keySet().contains(NamedOperation.class));
        assertNotNull(defaultResolvers.get(NamedOperation.class));
        assertTrue(defaultResolvers.get(NamedOperation.class) instanceof NamedOperationScoreResolver);

    }

    @Test
    public void shouldReAddDefaultScoreResolversWhenCallingSetMethod() {
        // Given
        final ScoreOperationChainHandler handler = new ScoreOperationChainHandler();
        final Map<Class<? extends Operation>, ScoreResolver> defaultScoreResolvers =
                ScoreOperationChainHandler.getDefaultScoreResolvers();

        final Map<Class<? extends Operation>, ScoreResolver> expectedMap = new HashMap<>();
        expectedMap.putAll(defaultScoreResolvers);

        final Map<Class<? extends Operation>, ScoreResolver> inputMap = new HashMap<>();
        inputMap.put(ToCsv.class, new DefaultScoreResolver(null));
        inputMap.put(ToStream.class, new DefaultScoreResolver(null));

        expectedMap.putAll(inputMap);

        // When
        handler.setScoreResolvers(inputMap);
        final Map<Class<? extends Operation>, ScoreResolver> results = handler.getScoreResolvers();

        // Then
        assertEquals(expectedMap.keySet(), results.keySet());
        assertTrue(results.get(NamedOperation.class) instanceof NamedOperationScoreResolver);
        assertEquals(expectedMap.size(), results.size());
    }
}
