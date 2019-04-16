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
package uk.gov.gchq.maestro.commonutil.stream;

import uk.gov.gchq.maestro.commonutil.iterable.LimitedInMemorySortedIterable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Java 8 {@link Collector}s for Maestro, based on the {@link java.util.stream.Collectors}
 * class.
 * <p>
 * Please note that using a {@link Collector} to gather together
 * the items contained in a {@link java.util.stream.Stream} will result in those
 * items being loaded into memory.
 */
public final class MaestroCollectors {

    private MaestroCollectors() {
        // Empty
    }

    /**
     * Returns a {@link Collector} that accumulates the input
     * items into a {@link LinkedHashSet}.
     *
     * @param <T> the type of the input items
     * @return a {@link Collector} which collects all the input
     * elements into a {@link LinkedHashSet}
     */
    public static <T> Collector<T, ?, Set<T>> toLinkedHashSet() {
        return new MaestroCollectorImpl<>(
                (Supplier<Set<T>>) LinkedHashSet::new,
                Set::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                set -> set
        );
    }

    /**
     * <p>
     * Returns a {@link Collector} that accumulates the input
     * items into a {@link LimitedInMemorySortedIterable}.
     * </p>
     * <p>
     * The usage of a {@link LimitedInMemorySortedIterable}
     * ensures that only relevant results are stored in memory, as the output is
     * built up incrementally.
     * </p>
     *
     * @param comparator  the {@link Comparator} to use when comparing
     *                    items
     * @param limit       the maximum number of items to collect
     * @param deduplicate true if the results should be deduplicated based the items hashcode/equals methods
     * @param <T>         the type of input items
     * @return a {@link Collector} which collects all the input
     * elements into a {@link LimitedInMemorySortedIterable}
     */
    public static <T> Collector<T, LimitedInMemorySortedIterable<T>, LimitedInMemorySortedIterable<T>> toLimitedInMemorySortedIterable(final Comparator<T> comparator, final Integer limit, final boolean deduplicate) {
        return new MaestroCollectorImpl<>(
                () -> new LimitedInMemorySortedIterable<>(comparator, limit, deduplicate),
                LimitedInMemorySortedIterable::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                }
        );
    }

    /**
     * Simple implementation class for {@code MaestroCollector}.
     *
     * @param <T> the type of elements to be collected
     * @param <R> the type of the result
     */
    static class MaestroCollectorImpl<T, A, R> implements Collector<T, A, R> {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;

        MaestroCollectorImpl(final Supplier<A> supplier,
                            final BiConsumer<A, T> accumulator,
                            final BinaryOperator<A> combiner,
                            final Function<A, R> finisher) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
        }

        MaestroCollectorImpl(final Supplier<A> supplier,
                            final BiConsumer<A, T> accumulator,
                            final BinaryOperator<A> combiner) {
            this(supplier, accumulator, combiner, i -> (R) i);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return new HashSet<>();
        }
    }
}
