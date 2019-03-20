/*
 * Copyright 2016-2019 Crown Copyright
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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A {@code LimitedCloseableIterable} is an {@link Iterable} which is limited to
 * a maximum size.
 *
 * @param <T> the type of items in the iterable.
 */
public class LimitedCloseableIterable<T> implements CloseableIterable<T> {
    private final CloseableIterable<T> iterable;
    private final int start;
    private final Integer end;
    private final Boolean truncate;

    public LimitedCloseableIterable(final Iterable<T> iterable, final int start, final Integer end) {
        this(new WrappedCloseableIterable<>(iterable), start, end);
    }

    public LimitedCloseableIterable(final Iterable<T> iterable, final int start, final Integer end, final Boolean truncate) {
        this(new WrappedCloseableIterable<>(iterable), start, end, truncate);
    }

    public LimitedCloseableIterable(final CloseableIterable<T> iterable, final int start, final Integer end) {
        this(iterable, start, end, true);
    }

    public LimitedCloseableIterable(final CloseableIterable<T> iterable, final int start, final Integer end, final Boolean truncate) {
        if (null != end && start > end) {
            throw new IllegalArgumentException("The start pointer must be less than the end pointer.");
        }

        if (null == iterable) {
            this.iterable = new EmptyClosableIterable<>();
        } else {
            this.iterable = iterable;
        }

        this.start = start;
        this.end = end;
        this.truncate = truncate;

    }

    @JsonIgnore
    public int getStart() {
        return start;
    }

    @JsonIgnore
    public Integer getEnd() {
        return end;
    }

    @Override
    public void close() {
        iterable.close();
    }

    @Override
    public CloseableIterator<T> iterator() {
        return new LimitedCloseableIterator<>(iterable.iterator(), start, end, truncate);
    }
}
