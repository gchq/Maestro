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

package uk.gov.gchq.maestro.named.operation;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.named.operation.serialisation.NamedOperationTypeReference;
import uk.gov.gchq.maestro.operation.io.Output;

import java.util.Map;

/**
 * A {@link GetAllNamedOperations} is an
 * {@link uk.gov.gchq.maestro.operation.Operation}
 * for retrieving all {@link NamedOperation}s associated with a Maestro
 * instance.
 */
@JsonPropertyOrder(value = {"class"}, alphabetic = true)
@Since("1.0.0")
@Summary("Gets all available named operations")
public class GetAllNamedOperations implements
        Output<CloseableIterable<NamedOperationDetail>> {
    private Map<String, String> options;

    @Override
    public TypeReference<CloseableIterable<NamedOperationDetail>> getOutputTypeReference() {
        return new NamedOperationTypeReference.IterableNamedOperationDetail();
    }

    @Override
    public GetAllNamedOperations shallowClone() {
        return new GetAllNamedOperations.Builder()
                .options(options)
                .build();
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public GetAllNamedOperations options(final Map<String, String> options) {
        this.options = options;
        return this;
    }

    public static class Builder extends BaseBuilder<GetAllNamedOperations, Builder>
            implements Output.Builder<GetAllNamedOperations, CloseableIterable<NamedOperationDetail>, Builder> {
        public Builder() {
            super(new GetAllNamedOperations());
        }
    }
}
