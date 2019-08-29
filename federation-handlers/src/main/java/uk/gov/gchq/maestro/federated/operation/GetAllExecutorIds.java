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

package uk.gov.gchq.maestro.federated.operation;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.maestro.executor.operation.io.Output;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;

import java.util.Map;
import java.util.Objects;

@Since("0.0.1")
@Summary("Gets all the Executor Ids for the current User.")
@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class GetAllExecutorIds implements Output<Iterable<? extends String>>, Operation {

    protected Map<String, String> options;

    @Override
    public TypeReference<Iterable<? extends String>> getOutputTypeReference() {
        return new TypeReferenceImpl.IterableString();
    }

    @Override
    public GetAllExecutorIds shallowClone() throws CloneFailedException {
        return new GetAllExecutorIds()
                .options(options);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .toHashCode();
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public GetAllExecutorIds options(final Map<String, String> options) {
        if (Objects.nonNull(options)) {
            this.options = options;
        } else {
            this.options.clear();
        }
        return (GetAllExecutorIds) this;
    }
}
