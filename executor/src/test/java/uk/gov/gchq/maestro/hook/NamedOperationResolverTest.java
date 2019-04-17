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

import com.google.common.collect.Maps;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.commonutil.exception.CacheOperationException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.named.operation.NamedOperation;
import uk.gov.gchq.maestro.named.operation.NamedOperationDetail;
import uk.gov.gchq.maestro.named.operation.ParameterDetail;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.handler.named.cache.NamedOperationCache;
import uk.gov.gchq.maestro.operation.impl.output.ToArray;
import uk.gov.gchq.maestro.operation.impl.output.ToList;
import uk.gov.gchq.maestro.operation.impl.output.ToSet;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


public class NamedOperationResolverTest extends HookTest<NamedOperationResolver> {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    public NamedOperationResolverTest() {
        super(NamedOperationResolver.class);
    }

    @Test
    public void shouldResolveNamedOperation() throws CacheOperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);

        final User user = mock(User.class);
        final NamedOperationDetail extendedNamedOperation = mock(NamedOperationDetail.class);

        final ToArray op1 = mock(ToArray.class);
        final ToList op2 = mock(ToList.class);
        final OperationChain namedOperationOpChain = new OperationChain(Arrays.asList(op1, op2));
        final Iterable<?> input = mock(CloseableIterable.class);

        final Map<String, Object> params = null;

        given(op1.getInput()).willReturn(null);
        given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);
        given(extendedNamedOperation.getOperationChain(params)).willReturn(namedOperationOpChain);

        final OperationChain<Object> opChain = new OperationChain.Builder()
                .first(new NamedOperation.Builder<>()
                        .name(opName)
                        .input(input)
                        .build())
                .build();

        // When
        resolver.preExecute(new Request(opChain, new Context(user)));

        // Then
        assertEquals(namedOperationOpChain.getOperations(), opChain.getOperations());

        verify(op1).setInput(input);
        verify(op2, never()).setInput(input);
    }

    @Test
    public void shouldResolveNestedNamedOperation() throws CacheOperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);

        final User user = mock(User.class);
        final NamedOperationDetail extendedNamedOperation = mock(NamedOperationDetail.class);

        final ToArray op1 = mock(ToArray.class);
        final ToList op2 = mock(ToList.class);
        final OperationChain namedOperationOpChain = new OperationChain(Arrays.asList(op1, op2));
        final Iterable<?> input = mock(CloseableIterable.class);

        final Map<String, Object> params = null;

        given(op1.getInput()).willReturn(null);
        given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);
        given(extendedNamedOperation.getOperationChain(params)).willReturn(namedOperationOpChain);

        final OperationChain<Object> opChain = new OperationChain.Builder()
                .first(new OperationChain.Builder()
                        .first(new NamedOperation.Builder<>()
                                .name(opName)
                                .input(input)
                                .build())
                        .build())
                .build();

        // When
        resolver.preExecute(new Request(opChain, new Context(user)));

        // Then
        assertEquals(1, opChain.getOperations().size());
        final OperationChain<?> nestedOpChain = (OperationChain<?>) opChain.getOperations().get(0);
        assertEquals(namedOperationOpChain.getOperations(), nestedOpChain.getOperations());

        verify(op1).setInput(input);
        verify(op2, never()).setInput(input);
    }

    @Test
    public void shouldExecuteNamedOperationWithoutOverridingInput() throws CacheOperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);

        final User user = mock(User.class);
        final NamedOperationDetail extendedNamedOperation = mock(NamedOperationDetail.class);

        final ToArray op1 = mock(ToArray.class);
        final ToList op2 = mock(ToList.class);
        final OperationChain namedOpChain = new OperationChain(Arrays.asList(op1, op2));
        final Iterable<?> input = mock(CloseableIterable.class);
        final Map<String, Object> params = null;

        given(op1.getInput()).willReturn(mock(CloseableIterable.class));
        given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);
        given(extendedNamedOperation.getOperationChain(params)).willReturn(namedOpChain);

        // When
        final OperationChain<Object> opChain = new OperationChain.Builder()
                .first(new NamedOperation.Builder<>()
                        .name(opName)
                        .input(input)
                        .build())
                .build();
        resolver.preExecute(new Request(opChain, new Context(user)));

        // Then
        assertSame(op1, opChain.getOperations().get(0));
        verify(op1, never()).setInput(input);
        assertSame(op2, opChain.getOperations().get(1));
        verify(op2, never()).setInput(input);
    }

    @Test
    public void shouldResolveNamedOperationWithParameter() throws CacheOperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);

        final User user = mock(User.class);

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("param1", 1L);

        ParameterDetail param = new ParameterDetail.Builder()
                .defaultValue(1L)
                .description("Limit param")
                .valueClass(Long.class)
                .build();
        Map<String, ParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put("param1", param);

        // Make a real NamedOperationDetail with a parameter
        final NamedOperationDetail extendedNamedOperation = new NamedOperationDetail.Builder()
                .operationName(opName)
                .description("standard operation")
                .operationChain("{ \"operations\": [ { \"class\":\"uk.gov" +
                        ".gchq.maestro.operation.impl.output.ToArray\" }, { " +
                        "\"class\":\"uk.gov.gchq.maestro.operation.impl.output" +
                        ".ToSet\", \"resultLimit\": \"${param1}\" } ] }")
                .parameters(paramDetailMap)
                .build();

        given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);

        final OperationChain<Object> opChain = new OperationChain.Builder()
                .first(new NamedOperation.Builder<>()
                        .name(opName)
                        .parameters(paramMap)
                        .build())
                .build();
        // When

        resolver.preExecute(new Request(opChain, new Context(user)));

        // Then
        assertEquals(opChain.getOperations().get(0).getClass(), ToArray.class);
        assertEquals(opChain.getOperations().get(1).getClass(), ToSet.class);

        // Check the parameter has been inserted
        // TODO FIX THIS WITH A VALUE TO USE AS A PARAM
        //assertEquals((long) ((ToSet) opChain.getOperations().get(1))
        // .getResultLimit(), 1L);
    }

    @Test
    public void shouldNotExecuteNamedOperationWithParameterOfWrongType() throws CacheOperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);

        final User user = mock(User.class);

        Map<String, Object> paramMap = Maps.newHashMap();
        // A parameter of the wrong type
        paramMap.put("param1", new ArrayList());

        ParameterDetail param = new ParameterDetail.Builder()
                .defaultValue(1L)
                .description("Limit param")
                .valueClass(Long.class)
                .build();
        Map<String, ParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put("param1", param);

        // Make a real NamedOperationDetail with a parameter
        final NamedOperationDetail extendedNamedOperation = new NamedOperationDetail.Builder()
                .operationName(opName)
                .description("standard operation")
                .operationChain("{ \"operations\": [ { \"class\":\"uk.gov.gchq.maestro.operation.impl.get.GetAllElements\" }, { \"class\":\"uk.gov.gchq.maestro.operation.impl.Limit\", \"resultLimit\": \"${param1}\" } ] }")
                .parameters(paramDetailMap)
                .build();

        given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);

        // When
        exception.expect(IllegalArgumentException.class);
        resolver.preExecute(new Request(new OperationChain.Builder()
                .first(new NamedOperation.Builder<>()
                        .name(opName)
                        .parameters(paramMap)
                        .build())
                .build(), new Context(user)));
    }

    @Test
    public void shouldNotExecuteNamedOperationWithWrongParameterName() throws OperationException, CacheOperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);

        final User user = mock(User.class);

        Map<String, Object> paramMap = Maps.newHashMap();
        // A parameter with the wrong name
        paramMap.put("param2", 1L);

        ParameterDetail param = new ParameterDetail.Builder()
                .defaultValue(1L)
                .description("Limit param")
                .valueClass(Long.class)
                .build();
        Map<String, ParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put("param1", param);

        // Make a real NamedOperationDetail with a parameter
        final NamedOperationDetail extendedNamedOperation = new NamedOperationDetail.Builder()
                .operationName(opName)
                .description("standard operation")
                .operationChain("{ \"operations\": [ { \"class\":\"uk.gov.gchq.maestro.operation.impl.get.GetAllElements\" }, { \"class\":\"uk.gov.gchq.maestro.operation.impl.Limit\", \"resultLimit\": \"${param1}\" } ] }")
                .parameters(paramDetailMap)
                .build();

        given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);

        // When
        exception.expect(IllegalArgumentException.class);
        resolver.preExecute(new Request(new OperationChain.Builder()
                .first(new NamedOperation.Builder<>()
                        .name(opName)
                        .parameters(paramMap)
                        .build())
                .build(), new Context(user)));
    }

    @Test
    public void shouldNotExecuteNamedOperationWithMissingRequiredArg() throws CacheOperationException {
        // Given
        final String opName = "opName";
        final NamedOperationCache cache = mock(NamedOperationCache.class);
        final NamedOperationResolver resolver = new NamedOperationResolver(cache);

        final User user = mock(User.class);

        // Don't set any parameters
        Map<String, Object> paramMap = Maps.newHashMap();

        ParameterDetail param = new ParameterDetail.Builder()
                .description("Limit param")
                .valueClass(Long.class)
                .required(true)
                .build();
        Map<String, ParameterDetail> paramDetailMap = Maps.newHashMap();
        paramDetailMap.put("param1", param);

        // Make a real NamedOperationDetail with a parameter
        final NamedOperationDetail extendedNamedOperation = new NamedOperationDetail.Builder()
                .operationName(opName)
                .description("standard operation")
                .operationChain("{ \"operations\": [ { \"class\":\"uk.gov.gchq.maestro.operation.impl.get.GetAllElements\" }, { \"class\":\"uk.gov.gchq.maestro.operation.impl.Limit\", \"resultLimit\": \"${param1}\" } ] }")
                .parameters(paramDetailMap)
                .build();

        given(cache.getNamedOperation(opName, user)).willReturn(extendedNamedOperation);

        // When
        exception.expect(IllegalArgumentException.class);
        resolver.preExecute(new Request(new OperationChain.Builder()
                .first(new NamedOperation.Builder<>()
                        .name(opName)
                        .parameters(paramMap)
                        .build())
                .build(), new Context(user)));
    }

    @Test
    public void shouldReturnOperationsInParameters() {
        // Given
        final NamedOperation namedOperation = new NamedOperation();
        Operation operation = new ToList<>();
        Map<String, Object> paramMap = Maps.newHashMap();

        paramMap.put("test param", operation);
        namedOperation.setParameters(paramMap);

        //When
        List<Operation> paramOperations = namedOperation.getOperations();
        Operation op = paramOperations.get(0);

        //Then
        assertEquals(paramOperations.size(), 1);
        assertEquals(op.getClass(), ToList.class);
    }

    @Override
    public NamedOperationResolver getTestObject() {
        return new NamedOperationResolver();
    }
}
