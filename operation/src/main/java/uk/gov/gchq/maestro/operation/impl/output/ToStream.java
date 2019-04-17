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

package uk.gov.gchq.maestro.operation.impl.output;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.maestro.operation.io.InputOutput;
import uk.gov.gchq.maestro.operation.io.MultiInput;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;

import java.util.Map;
import java.util.stream.Stream;

/**
 * A {@code ToStream} operation takes in an {@link Iterable} of
 * items and converts them to a {@link Stream}.
 *
 * @see uk.gov.gchq.maestro.operation.impl.output.ToStream.Builder
 */
@JsonPropertyOrder(value = {"class", "input"}, alphabetic = true)
@Since("1.0.0")
@Summary("Converts an Iterable to a Stream")
public class ToStream<T> implements
        InputOutput<Iterable<? extends T>, Stream<? extends T>>,
        MultiInput<T> {
    private Iterable<? extends T> input;
    private Map<String, String> options;

    @Override
    public Iterable<? extends T> getInput() {
        return input;
    }

    @Override
    public void setInput(final Iterable<? extends T> input) {
        this.input = input;
    }

    @Override
    public TypeReference<Stream<? extends T>> getOutputTypeReference() {
        return new TypeReferenceImpl.Stream();
    }

    @Override
    public ToStream<T> shallowClone() {
        return new ToStream.Builder<T>()
                .input(input)
                .options(options)
                .build();
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public ToStream options(final Map<String, String> options) {
        this.options = options;
        return this;
    }

    public static final class Builder<T>
            extends BaseBuilder<ToStream<T>, Builder<T>>
            implements InputOutput.Builder<ToStream<T>, Iterable<? extends T>, Stream<? extends T>, Builder<T>>,
            MultiInput.Builder<ToStream<T>, T, Builder<T>> {
        public Builder() {
            super(new ToStream<>());
        }
    }
}
