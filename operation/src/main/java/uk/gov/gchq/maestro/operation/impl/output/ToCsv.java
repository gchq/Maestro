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
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.maestro.commonutil.Required;
import uk.gov.gchq.maestro.data.generator.StringGenerator;
import uk.gov.gchq.maestro.operation.io.InputOutput;
import uk.gov.gchq.maestro.operation.io.MultiInput;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;

import java.util.Map;

/**
 * A {@code ToMap} operation takes in an {@link Iterable} of items
 * and uses a {@link uk.gov.gchq.maestro.data.generator.StringGenerator} to
 * convert
 * each item into a CSV String.
 *
 * @see ToCsv.Builder
 */
@JsonPropertyOrder(value = {"class", "input", "elementGenerator"}, alphabetic = true)
@Since("1.0.0")
@Summary("Converts objects to CSV Strings")
public class ToCsv<T> implements
        InputOutput<Iterable<? extends T>, Iterable<? extends String>>,
        MultiInput<T> {

    @Required
    private StringGenerator stringGenerator;
    private Iterable<? extends T> input;
    private Map<String, String> options;

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    public StringGenerator getElementGenerator() {
        return stringGenerator;
    }

    void setStringGenerator(final StringGenerator stringGenerator) {
        this.stringGenerator = stringGenerator;
    }

    @Override
    public Iterable<? extends T> getInput() {
        return input;
    }

    @Override
    public void setInput(final Iterable input) {
        this.input = input;
    }

    @Override
    public TypeReference<Iterable<? extends String>> getOutputTypeReference() {
        return new TypeReferenceImpl.IterableString();
    }

    @Override
    public ToCsv shallowClone() {
        return new ToCsv.Builder<T>()
                .generator(stringGenerator)
                .input(input)
                .options(options)
                .build();
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public ToCsv options(final Map<String, String> options) {
        this.options = options;
        return this;
    }

    public static final class Builder<T>
            extends BaseBuilder<ToCsv<T>, Builder<T>>
            implements InputOutput.Builder<ToCsv<T>, Iterable<? extends T>, Iterable<? extends String>, Builder<T>>,
            MultiInput.Builder<ToCsv<T>, T, ToCsv.Builder<T>> {
        public Builder() {
            super(new ToCsv<>());
        }

        /**
         * @param generator the
         *                  {@link uk.gov.gchq.maestro.data.generator.StringGenerator} to
         *                  set on the operation
         * @return this Builder
         */
        public ToCsv.Builder<T> generator(final StringGenerator generator) {
            _getOp().setStringGenerator(generator);
            return _self();
        }
    }
}
