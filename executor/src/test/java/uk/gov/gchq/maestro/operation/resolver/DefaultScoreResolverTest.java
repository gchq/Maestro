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

package uk.gov.gchq.maestro.operation.resolver;

import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.helper.TestOperationsImpl;
import uk.gov.gchq.maestro.named.operation.NamedOperation;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.impl.DiscardOutput;
import uk.gov.gchq.maestro.operation.impl.output.ToArray;
import uk.gov.gchq.maestro.operation.impl.output.ToList;
import uk.gov.gchq.maestro.operation.impl.output.ToSet;
import uk.gov.gchq.maestro.operation.impl.output.ToStream;
import uk.gov.gchq.maestro.operation.resolver.named.NamedOperationScoreResolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

public class DefaultScoreResolverTest {
    @Test
    public void shouldGetDefaultScoreWhenNoOperationScores() {
        // Given
        final DefaultScoreResolver resolver = new DefaultScoreResolver(new LinkedHashMap<>());

        final ToList op1 = mock(ToList.class);

        // When
        final int score = resolver.getScore(op1);

        // Then
        assertEquals(1, score);
    }

    @Test
    public void shouldGetScore() {
        // Given
        final ToList op1 = mock(ToList.class);

        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(ToList.class, 2);
        opScores.put(ToSet.class, 1);
        opScores.put(ToArray.class, 1);
        final DefaultScoreResolver resolver = new DefaultScoreResolver(opScores);

        // When
        final int score = resolver.getScore(op1);

        // Then
        assertEquals(2, score);
    }

    @Test
    public void shouldGetScoreForOperationChain() {
        // Given
        final ToList toList = mock(ToList.class);
        final ToSet toSet = mock(ToSet.class);
        final ToArray toArray = mock(ToArray.class);
        final List<Operation> opList = Arrays.asList(toList, toSet, toArray);
        final OperationChain opChain = mock(OperationChain.class);
        given(opChain.getOperations()).willReturn(opList);

        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(ToSet.class, 2);
        opScores.put(ToList.class, 3);
        opScores.put(ToArray.class, 1);

        final DefaultScoreResolver resolver = new DefaultScoreResolver(opScores);

        // When
        final int score = resolver.getScore(opChain);

        // Then
        assertEquals(6, score);
    }

    @Test
    public void shouldGetScoreForNestedOperations() {
        // Given
        final ToSet toSet = mock(ToSet.class);
        final TestOperationsImpl testOperations = mock(TestOperationsImpl.class);
        final ToList toList = mock(ToList.class);
        given(testOperations.getOperations()).willReturn(
                Collections.singletonList(new OperationChain<>(toList, toList)));
        final ToArray toArray = mock(ToArray.class);
        final List<Operation> opList = Arrays.asList(toSet, testOperations, toArray);
        final OperationChain opChain = mock(OperationChain.class);
        given(opChain.getOperations()).willReturn(opList);

        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(ToSet.class, 2);
        opScores.put(ToList.class, 2);
        opScores.put(ToArray.class, 1);

        final DefaultScoreResolver resolver = new DefaultScoreResolver(opScores);

        // When
        final int score = resolver.getScore(opChain);

        // Then
        assertEquals(7, score);
    }


    @Test
    public void shouldGetOperationChainScore() throws OperationException {
        // Given
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver();
        final OperationChain opChain = new OperationChain.Builder()
                .first(mock(ToList.class))
                .then(mock(ToSet.class))
                .build();

        // When
        final Object result = scoreResolver.getScore(opChain);

        // Then
        assertSame(2, result);
    }

    @Test
    public void shouldGetScoreForOperationChainWithNestedOperationChain() throws OperationException {
        // Given
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver();

        final OperationChain opChain = new OperationChain.Builder()
                .first(new OperationChain.Builder()
                        .first(mock(ToList.class))
                        .then(mock(ToSet.class))
                        .build())
                .then(mock(ToArray.class))
                .build();

        // When
        final Object result = scoreResolver.getScore(opChain);

        // Then
        assertSame(3, result);
    }

    @Test
    public void shouldGetScoreForOperationChainContainingNamedOperation() {
        // Given
        final ScoreResolver mockResolver = mock(NamedOperationScoreResolver.class);

        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(ToList.class, 2);
        opScores.put(ToSet.class, 3);
        opScores.put(ToArray.class, 4);

        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();

        final String opName = "basicOp";
        final NamedOperation<Object, Object> namedOp =
                new NamedOperation.Builder<>()
                        .name(opName)
                        .build();
        resolvers.put(NamedOperation.class, mockResolver);
        given(mockResolver.getScore(eq(namedOp), any())).willReturn(5);

        final OperationChain opChain = new OperationChain.Builder()
                .first(new ToList())
                .then(new ToSet())
                .then(new ToArray())
                .then(new DiscardOutput())
                .then(new TestOperationsImpl(Arrays.asList(namedOp,
                        new ToSet())))
                .build();

        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver(opScores, resolvers);

        // When
        final Object result = scoreResolver.getScore(opChain);

        // Then
        assertEquals(17, result);
    }

    @Test
    public void shouldPreventInfiniteRecusion() {
        // Given
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();
        resolvers.put(ToSet.class, new ScoreResolver() {
                    @Override
                    public Integer getScore(final Operation operation) {
                        throw new IllegalArgumentException("defaultResolver is required");
                    }

                    @Override
                    public Integer getScore(final Operation operation, final ScoreResolver defaultScoreResolver) {
                        // infinite loop
                        return defaultScoreResolver.getScore(operation);
                    }
                }
        );

        final OperationChain opChain = new OperationChain.Builder()
                .first(new ToList())
                .then(new ToSet())
                .build();

        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver(null, resolvers);

        // When
        final Object result = scoreResolver.getScore(opChain);

        // Then
        assertEquals(2, result);
    }

    @Test
    public void shouldGetScoreForOperationChainWhenNamedOperationScoreIsNull() {
        // Given

        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(Operation.class, 1);
        opScores.put(ToList.class, 2);
        opScores.put(ToSet.class, 1);
        opScores.put(ToArray.class, 1);

        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();

        final String opName = "basicOp";
        final NamedOperation<Object, Object> namedOp =
                new NamedOperation.Builder<>()
                        .name(opName)
                        .build();
        final ScoreResolver mockResolver = mock(NamedOperationScoreResolver.class);
        resolvers.put(NamedOperation.class, mockResolver);
        given(mockResolver.getScore(eq(namedOp), any())).willReturn(null);

        final OperationChain opChain = new OperationChain.Builder()
                .first(new ToList())
                .then(new ToSet())
                .then(new ToArray())
                .then(namedOp)
                .build();

        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver(opScores, resolvers);

        // When
        final Object result = scoreResolver.getScore(opChain);

        // Then
        assertEquals(5, result);

    }

    @Test
    public void shouldGetScoreForOperationChainWithMultipleScoreResolvers() {
        // Given
        final Map<Class<? extends Operation>, ScoreResolver> resolvers = new HashMap<>();

        final ScoreResolver mockResolver = mock(NamedOperationScoreResolver.class);
        final ScoreResolver mockResolver1 = mock(DefaultScoreResolver.class);

        final ToSet op1 = new ToSet();
        final ToStream op2 = new ToStream();
        final Map<Class<? extends Operation>, Integer> opScores = new LinkedHashMap<>();
        opScores.put(ToSet.class, 2);

        final String opName = "namedOp";
        final NamedOperation namedOp = mock(NamedOperation.class);
        namedOp.setOperationName(opName);

        resolvers.put(namedOp.getClass(), mockResolver);
        resolvers.put(op2.getClass(), mockResolver1);

        given(mockResolver.getScore(eq(namedOp), any())).willReturn(3);
        given(mockResolver1.getScore(eq(op2), any())).willReturn(5);

        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver(opScores, resolvers);

        final OperationChain opChain = new OperationChain.Builder()
                .first(op1)
                .then(op2)
                .then(namedOp)
                .build();

        // When
        final Object result = scoreResolver.getScore(opChain);

        // Then
        assertEquals(10, result);
    }

    @Test
    public void shouldGetScoreForNestedOperationWithNullOperationList() {
        // Given
        final ToSet op1 = mock(ToSet.class);
        final ToStream op2 = mock(ToStream.class);
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver();

        final OperationChain opChain = new OperationChain.Builder()
                .first(op1)
                .then(op2)
                .then(new OperationChain((List) null))
                .build();

        // When
        final Object result = scoreResolver.getScore(opChain);

        // Then
        assertEquals(2, result);
    }

    @Test
    public void shouldReturnZeroForANullOperationChain() {
        // Given
        final DefaultScoreResolver scoreResolver = new DefaultScoreResolver();

        final OperationChain opChain = null;

        // When
        final Object result = scoreResolver.getScore(opChain);

        // Then
        assertEquals(0, result);
    }
}
