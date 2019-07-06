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

package uk.gov.gchq.maestro;

import org.junit.Test;

import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.user.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ContextTest {
    @Test
    public void shouldConstructContextsWithTheSameUserAndGenerateDifferentJobIds() {
        // Given
        final User user = new User();

        // When
        final Context context1 = new Context(user);
        final Context context2 = new Context(user);

        // Then
        assertEquals(user, context1.getUser());
        assertEquals(user, context2.getUser());
        assertNotEquals(context1.getJobId(), context2.getJobId());
        assertTrue(context1.getExporters().isEmpty());
        assertTrue(context2.getExporters().isEmpty());
    }

    @Test
    public void shouldConstructContextWithUser() {
        // Given
        final User user = new User();

        // When
        final Context context = new Context.Builder()
                .user(user)
                .build();

        // Then
        assertEquals(user, context.getUser());
        assertTrue(context.getExporters().isEmpty());
    }

    @Test
    public void shouldConstructContextWithUnknownUser() {
        // Given
        // When
        final Context context = new Context();

        // Then
        assertEquals(User.UNKNOWN_USER_ID, context.getUser().getUserId());
    }

    @Test
    public void shouldThrowExceptionIfUserIsNull() {
        // Given
        final User user = null;

        // When / Then
        try {
            new Context(user);
            fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            assertEquals("User is required", e.getMessage());
        }
    }

    @Test
    public void shouldConstructContextWithContext() {
        // Given
        final Context context = new Context.Builder()
                .user(new User())
                .build();
        final Operation exporter = new Operation("Exporter");
        context.addExporter(exporter);
        final OperationChain opChain = mock(OperationChain.class);
        final OperationChain opChainClone = mock(OperationChain.class);
        given(opChain.shallowClone()).willReturn(opChainClone);
        context.setOriginalOpChain(opChain);
        context.setConfig("key", "value");

        // When
        final Context clone = new Context(context);

        // Then
        assertSame(context.getUser(), clone.getUser());
        assertNotEquals(context.getJobId(), clone.getJobId());
        assertNotSame(context.getOriginalOpChain(), clone.getOriginalOpChain());
        assertSame(opChainClone, clone.getOriginalOpChain());
        assertEquals(1, clone.getExporters().size());
        assertSame(exporter, clone.getExporters().iterator().next());
        assertEquals(context.getConfig("key"), clone.getConfig("key"));
    }

    @Test
    public void shouldAddAndGetExporter() {
        // Given
        final Operation exporter = new Operation("Exporter");
        final Context context = new Context();

        // When
        context.addExporter(exporter);

        // Then
        assertSame(exporter, context.getExporter(exporter.getId()));
        assertSame(exporter, context.getExporter("Exporter"));

    }

    @Test
    public void shouldSetAndGetOriginalOpChain() {
        // Given
        final OperationChain opChain = mock(OperationChain.class);
        final Context context = new Context();

        // When
        context.setOriginalOpChain(opChain);

        // Then
        assertSame(opChain, context.getOriginalOpChain());
    }
}
