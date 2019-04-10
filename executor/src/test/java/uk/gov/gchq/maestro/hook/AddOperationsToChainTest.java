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

/*
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.junit.Test;

import uk.gov.gchq.koryphe.impl.predicate.Exists;
import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.commonutil.JsonAssert;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.helper.TestOperationsImpl;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.impl.DiscardOutput;
import uk.gov.gchq.maestro.operation.impl.output.ToCsv;
import uk.gov.gchq.maestro.operation.impl.output.ToSingletonList;
import uk.gov.gchq.maestro.operation.impl.output.ToStream;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AddOperationsToChainTest extends HookTest<AddOperationsToChain> {
    private static final String ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH = "addOperationsToChain.json";

    public AddOperationsToChainTest() {
        super(AddOperationsToChain.class);
    }

    @Test
    public void shouldAddAllOperationsToGetWalksOperation() throws SerialisationException {
        // Given
        final AddOperationsToChain hook = new AddOperationsToChain();
        final Map<String, List<Operation>> after = new HashMap<>();
        after.put(ToSingletonList.class.getName(),
                Lists.newArrayList(new ToCsv<>()));
        hook.setAfter(after);
        hook.setEnd(Lists.newArrayList(new ToStream<>()));

        final ToSingletonList toSingletonList = new ToSingletonList();
        final ToCsv toCsv = new ToCsv();

        final OperationChain opOps = new OperationChain.Builder()
                .first(toSingletonList)
                .build();

        final TestOperationsImpl testOperations =
                new TestOperationsImpl(Arrays.asList(opOps));

        final OperationChain opChain = new OperationChain.Builder()
                .first(testOperations)
                .build();

        // When
        hook.preExecute(new Request(opChain, new Context(new User())));

        // Then
        final TestOperationsImpl expectedOp =
                new TestOperationsImpl(Arrays.asList(new OperationChain.Builder().first(toSingletonList).then(toCsv).build()));

        final OperationChain expectedOpChain = new OperationChain.Builder()
                .first(expectedOp)
                .then(new ToStream<>())
                .build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldAddAllOperationsWithNoAuthsGivenPath() throws IOException {
        // Given
        AddOperationsToChain hook = fromJson(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation count = new Count<>();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();
        Operation limit = new Limit<>();

        final OperationChain opChain = new OperationChain.Builder()
                .first(getAdjacentIds)
                .then(getElements)
                .then(getAllElements)
                .build();

        // When
        hook.preExecute(opChain, new Context(new User()));

        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder()
                .first(discardOutput)
                .then(splitStore)
                .then(validate)
                .then(getAdjacentIds)
                .then(count)
                .then(discardOutput)
                .then(countGroups)
                .then(getElements)
                .then(getAllElements)
                .then(limit)
                .then(validate)
                .then(count)
                .build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldAddAllOperationsWithFirstAuthsGivenPath() throws IOException {
        // Given
        AddOperationsToChain hook = fromJson(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        User user = new User.Builder().opAuths("auth1", "auth2").build();

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();

        final OperationChain opChain = new OperationChain.Builder()
                .first(getAdjacentIds)
                .then(getElements)
                .then(getAllElements)
                .build();

        // When
        hook.preExecute(opChain, new Context(user));

        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder()
                .first(discardOutput)
                .then(getAdjacentIds)
                .then(getElements)
                .then(getAllElements)
                .then(splitStore)
                .build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldAddAllOperationsWithSecondAuthsGivenPath() throws IOException {
        // Given
        AddOperationsToChain hook = fromJson(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        User user = new User.Builder().opAuths("auth2").build();

        Operation splitStore = new SplitStoreFromFile();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();

        final OperationChain opChain = new OperationChain.Builder()
                .first(getAdjacentIds)
                .then(getElements)
                .then(getAllElements)
                .build();

        // When
        hook.preExecute(opChain, new Context(user));

        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder()
                .first(validate)
                .then(getAdjacentIds)
                .then(countGroups)
                .then(getElements)
                .then(getAllElements)
                .then(splitStore)
                .build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldAddAllOperationsGivenJson() throws IOException {
        // Given
        final byte[] bytes;
        try (final InputStream inputStream = StreamUtil.openStream(getClass(), ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH)) {
            bytes = IOUtils.toByteArray(inputStream);
        }
        final AddOperationsToChain hook = fromJson(bytes);

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation count = new Count<>();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();
        Operation limit = new Limit<>();

        final OperationChain opChain = new OperationChain.Builder()
                .first(getAdjacentIds)
                .then(getElements)
                .then(getAllElements)
                .build();

        // When
        hook.preExecute(opChain, new Context(new User()));

        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder()
                .first(discardOutput)
                .then(splitStore)
                .then(validate)
                .then(getAdjacentIds)
                .then(count)
                .then(discardOutput)
                .then(countGroups)
                .then(getElements)
                .then(getAllElements)
                .then(limit)
                .then(validate)
                .then(count)
                .build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldThrowExceptionWhenAddingNullExtraOperation() throws IOException {
        // Given
        final String nullTestJson = "{\"class\": \"uk.gov.gchq.maestro.graph.hook.AddOperationsToChain\", \"start\":[{\"class\": null}]}";

        //When / Then
        try {
            fromJson(nullTestJson.getBytes());
            fail("Exception expected");
        } catch (final RuntimeException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("'null'"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingEmptyExtraOperation() throws IOException {
        // Given
        final String emptyTestJson = "{\"class\": \"uk.gov.gchq.maestro.graph.hook.AddOperationsToChain\", \"start\":[{\"class\": \"\"}]}";

        //When / Then
        try {
            fromJson(emptyTestJson.getBytes());
            fail("Exception expected");
        } catch (final RuntimeException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("''"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingFalseExtraOperation() throws IOException {
        // Given
        final String falseOperationTestJson = "{\"class\": \"uk.gov.gchq.maestro.graph.hook.AddOperationsToChain\", \"start\":[{\"class\": \"this.Operation.Doesnt.Exist\"}]}";

        //When / Then
        try {
            fromJson(falseOperationTestJson.getBytes());
            fail("Exception expected");
        } catch (final RuntimeException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("'this.Operation.Doesnt.Exist'"));
        }
    }

    @Test
    public void shouldClearListWhenAddingOperations() throws IOException {
        //Given
        final AddOperationsToChain hook = fromJson(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
        hook.setBefore(null);
        hook.setAfter(null);

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation count = new Count<>();
        Operation getElements = new GetElements();

        final OperationChain opChain = new OperationChain.Builder()
                .first(getElements)
                .build();

        // When
        hook.preExecute(opChain, new Context(new User()));

        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder()
                .first(discardOutput)
                .then(splitStore)
                .then(getElements)
                .then(count)
                .build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldHandleNestedOperationChain() throws SerialisationException {
        // Given
        AddOperationsToChain hook = fromJson(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation count = new Count<>();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        If ifOp = new If.Builder<>()
                .conditional(new Conditional(new Exists(), new GetElements()))
                .then(new GetElements())
                .otherwise(new GetAllElements())
                .build();
        Operation getAllElements = new GetAllElements();
        Operation limit = new Limit<>();

        final OperationChain opChain = new OperationChain.Builder()
                .first(getAdjacentIds)
                .then(new OperationChain.Builder()
                        .first(getElements)
                        .then(getAllElements)
                        .build())
                .then(ifOp)
                .build();

        // When
        hook.preExecute(opChain, new Context(new User()));

        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder()
                .first(discardOutput)
                .then(splitStore)
                .then(validate)
                .then(getAdjacentIds)
                .then(count)
                .then(discardOutput)
                .then((Operation) new OperationChain.Builder()
                        .first(countGroups)
                        .then(getElements)
                        .then(getAllElements)
                        .then(limit)
                        .then(validate)
                        .build())
                .then(new If.Builder<>()
                        .conditional(new Conditional(new Exists(), new OperationChain<>(new CountGroups(), new GetElements())))
                        .then(new OperationChain<>(new CountGroups(), new GetElements()))
                        .otherwise(new OperationChain<>(new GetAllElements(), new Limit<>(), new Validate()))
                        .build())
                .then(new Count())
                .build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldHandleIfOperationWithNoConditionalOrOtherwise() throws SerialisationException {
        // Given
        AddOperationsToChain hook = fromJson(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        If ifOp = new If.Builder<>()
                .then(new GetElements())
                .build();

        final OperationChain opChain = new OperationChain.Builder()
                .first(ifOp)
                .build();

        // When
        hook.preExecute(opChain, new Context(new User()));

        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder()
                .first(discardOutput)
                .then(splitStore)
                .then(new If.Builder<>()
                        .then(new OperationChain<>(new CountGroups(), new GetElements()))
                        .build())
                .then(new Count())
                .build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldFailQuietlyIfNestedOperationsCannotBeModified() throws SerialisationException {
        // Given
        AddOperationsToChain hook = fromJson(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation count = new Count<>();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();
        TestUnmodifiableOperationsImpl nestedUnmodifiableOps = new TestUnmodifiableOperationsImpl(Arrays.asList(getAllElements, getElements));

        final OperationChain opChain = new OperationChain.Builder()
                .first(getAdjacentIds)
                .then(nestedUnmodifiableOps)
                .build();

        // When
        hook.preExecute(opChain, new Context(new User()));

        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder()
                .first(discardOutput)
                .then(splitStore)
                .then(validate)
                .then(getAdjacentIds)
                .then(count)
                .then(discardOutput)
                .then(nestedUnmodifiableOps)
                .then(count)
                .build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldAddIfOperation() throws SerialisationException {
        // Given
        final GetWalks getWalks = new GetWalks();
        final uk.gov.gchq.maestro.operation.impl.Map map = new uk.gov.gchq.maestro.operation.impl.Map();
        final ToVertices toVertices = new ToVertices();
        final ToSet toSet = new ToSet();
        final Exists exists = new Exists();
        final Limit limit = new Limit();
        final GetAllElements getAllElements = new GetAllElements();
        final GetElements getElements = new GetElements();

        final Conditional conditional = new Conditional();
        conditional.setPredicate(exists);

        final If ifOp = new If.Builder<>()
                .conditional(conditional)
                .then(getElements)
                .otherwise(getAllElements)
                .build();

        final AddOperationsToChain hook = new AddOperationsToChain();

        final Map<String, List<Operation>> after = new HashMap<>();
        final List<Operation> afterOps = new LinkedList<>();
        afterOps.add(ifOp);
        afterOps.add(limit);
        after.put("uk.gov.gchq.maestro.operation.impl.output.ToSet", afterOps);
        hook.setAfter(after);

        final OperationChain opChain = new OperationChain.Builder()
                .first(getWalks)
                .then(map)
                .then(toVertices)
                .then(toSet)
                .build();

        // When
        hook.preExecute(opChain, new Context());

        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder()
                .first(getWalks)
                .then(map)
                .then(toVertices)
                .then(toSet)
                .then(ifOp)
                .then(limit)
                .build();

        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain),
                JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldReturnClonedOperations() throws IOException {
        // Given
        final AddOperationsToChain hook = fromJson(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);

        // When / Then
        assertClonedOperations(hook.getStart(), hook.getStart());
        assertClonedOperations(hook.getBefore(), hook.getBefore());
        assertClonedOperations(hook.getAfter(), hook.getAfter());
        assertClonedOperations(hook.getEnd(), hook.getEnd());
    }

    public void assertClonedOperations(final Map<String, List<Operation>> after1, final Map<String, List<Operation>> after2) {
        for (final Map.Entry<String, List<Operation>> entry1 : after1.entrySet()) {
            final List<Operation> ops1 = entry1.getValue();
            final List<Operation> ops2 = after2.get(entry1.getKey());
            assertClonedOperations(ops1, ops2);
        }
    }

    public void assertClonedOperations(final List<Operation> ops1, final List<Operation> ops2) {
        assertEquals(ops1.size(), ops2.size());
        for (int i = 0; i < ops1.size(); i++) {
            assertEquals(ops1.get(i).getClass(), ops2.get(i).getClass());
            assertNotSame(ops1.get(i), ops2.get(i));
        }
    }

    @Override
    protected AddOperationsToChain getTestObject() {
        return fromJson(ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
    }
}
*/
