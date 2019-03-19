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

import uk.gov.gchq.maestro.commonutil.CloseableUtil;

import java.util.Iterator;

/**
 * A {@code WrappedCloseableIterator} is an {@link CloseableIterator} which provides
 * a wrapper around any non-closeable iterables.
 *
 * @param <T> the type of items in the iterable.
 */
public class WrappedCloseableIterator<T> implements CloseableIterator<T> {
    private final Iterator<T> iterator;

    public WrappedCloseableIterator() {
        this(null);
    }

    public WrappedCloseableIterator(final Iterator<T> iterator) {
        if (null == iterator) {
            this.iterator = new EmptyCloseableIterator<>();
        } else {
            this.iterator = iterator;
        }
    }

    @Override
    public void close() {
        CloseableUtil.close(iterator);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
