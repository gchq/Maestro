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

package uk.gov.gchq.maestro.commonutil.iterable;

import org.junit.Test;

import java.util.Iterator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WrappedCloseableIteratorTest {

    @Test
    public void shouldDelegateCloseToWrappedIterator() {
        // Given
        final CloseableIterator<Object> closeableIterator = mock(CloseableIterator.class);
        final WrappedCloseableIterator<Object> wrappedIterator = new WrappedCloseableIterator<>(closeableIterator);

        // When
        wrappedIterator.close();

        // Then
        verify(closeableIterator).close();
    }

    @Test
    public void shouldDoNothingWhenCloseCalledOnNoncloseableIterator() {
        // Given
        final Iterator<Object> iterator = mock(Iterator.class);
        final WrappedCloseableIterator<Object> wrappedIterator = new WrappedCloseableIterator<>(iterator);

        // When
        wrappedIterator.close();

        // Then - no exception
    }
}
