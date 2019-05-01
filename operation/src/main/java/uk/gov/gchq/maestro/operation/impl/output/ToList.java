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

import java.util.List;
import java.util.Map;

/**
 * A {@code ToList} operation takes in an {@link Iterable} of items
 * and converts them to a {@link List}.
 *
 * @see uk.gov.gchq.maestro.operation.impl.output.ToList.Builder
 */
@JsonPropertyOrder(value = {"class", "input"}, alphabetic = true)
@Since("0.0.1")
@Summary("Converts an Iterable to a List")
public class ToList<T> implements
        InputOutput<Iterable<? extends T>, List<? extends T>>,
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
    public TypeReference<List<? extends T>> getOutputTypeReference() {
        return new TypeReferenceImpl.List();
    }

    @Override
    public ToList<T> shallowClone() {
        return new ToList.Builder<T>()
                .input(input)
                .options(options)
                .build();
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public ToList options(final Map<String, String> options) {
        this.options = options;
        return this;
    }

    public static final class Builder<T>
            extends BaseBuilder<ToList<T>, Builder<T>>
            implements InputOutput.Builder<ToList<T>, Iterable<? extends T>, List<? extends T>, Builder<T>>,
            MultiInput.Builder<ToList<T>, T, Builder<T>> {
        public Builder() {
            super(new ToList<>());
        }
    }
}
