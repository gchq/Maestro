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
package uk.gov.gchq.maestro.operation;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Since("0.0.1")
@Summary("Wraps an operation which is not explicitly supported.")
@JsonPropertyOrder(value = {"wrappedOp"}, alphabetic = true)
public class DefaultOperation implements Operation {

    private Map<String, String> options = new HashMap<>();
    private Operation wrappedOp;

    public DefaultOperation setWrappedOp(final Operation operation) {
        this.wrappedOp = operation;
        return this;
    }

    public Operation getWrappedOp() {
        return wrappedOp;
    }

    @Override
    public DefaultOperation shallowClone() throws CloneFailedException {
        return new DefaultOperation().options(options);
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public DefaultOperation options(final Map<String, String> options) {
        if (Objects.nonNull(options)) {
            this.options = options;
        } else {
            this.options.clear();
        }
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DefaultOperation that = (DefaultOperation) o;

        return new EqualsBuilder()
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .toHashCode();
    }
}
