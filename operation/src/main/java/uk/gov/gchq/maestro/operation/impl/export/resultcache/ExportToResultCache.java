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

package uk.gov.gchq.maestro.operation.impl.export.resultcache;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.export.ExportTo;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;

import java.util.Map;
import java.util.Set;

/**
 * An {@code ExportToResultCache} Export operation exports results into
 * a cache. The cache is backed by a simple Maestro instance that can be configured.
 * The results can be of any type - as long as they are json serialisable.
 */
@JsonPropertyOrder(value = {"class", "input", "key"}, alphabetic = true)
@Since("1.0.0")
@Summary("Exports to a cache backed by a Maestro instance")
public class ExportToResultCache<T> implements
        ExportTo<T> {
    private String key;
    private Set<String> opAuths;
    private T input;
    private Map<String, String> options;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(final String key) {
        this.key = key;
    }

    public Set<String> getOpAuths() {
        return opAuths;
    }

    public void setOpAuths(final Set<String> opAuths) {
        this.opAuths = opAuths;
    }

    @Override
    public T getInput() {
        return input;
    }

    @Override
    public void setInput(final T input) {
        this.input = input;
    }

    @Override
    public ExportToResultCache<T> shallowClone() {
        return new ExportToResultCache.Builder<T>()
                .key(key)
                .opAuths(opAuths)
                .input(input)
                .options(options)
                .build();
    }

    @Override
    public TypeReference<T> getOutputTypeReference() {
        return (TypeReference) new TypeReferenceImpl.Object();
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public Operation options(final Map<String, String> options) {
        this.options = options;
        return this;
    }

    public static final class Builder<T> extends Operation.BaseBuilder<ExportToResultCache<T>, Builder<T>>
            implements ExportTo.Builder<ExportToResultCache<T>, T, Builder<T>> {
        public Builder() {
            super(new ExportToResultCache<>());
        }

        public Builder<T> opAuths(final Set<String> opAuths) {
            _getOp().setOpAuths(opAuths);
            return _self();
        }

        public Builder<T> opAuths(final String... opAuths) {
            _getOp().setOpAuths(Sets.newHashSet(opAuths));
            return _self();
        }
    }
}
