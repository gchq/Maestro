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

package uk.gov.gchq.maestro.federatedexecutor.operation;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.koryphe.binaryoperator.KorypheBinaryOperator;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Since("0.0.1")
@Summary("Federates the operation to sub executors and combines the different results using the mergeOperation")
@JsonPropertyOrder(value = {"class", "ids", "operation", "mergeOperation"}, alphabetic = true)
public class FederatedOperation implements Operation {

    private Operation operation;
    private Set<String> ids;
    private Map<String, String> options = new HashMap<>();
    private KorypheBinaryOperator mergeOperation;

    @Override
    public Operation shallowClone() throws CloneFailedException {
        return new FederatedOperation()
                .operation(operation)
                .ids(ids)
                .mergeOperation(mergeOperation)
                .options(options);
    }

    private FederatedOperation ids(final Set<String> ids) {
        this.ids = ids;
        return this;
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public FederatedOperation options(final Map<String, String> options) {
        if (Objects.nonNull(options)) {
            this.options = options;
        } else {
            this.options.clear();
        }
        return this;
    }

    public FederatedOperation operation(final Operation operation) {
        this.operation = operation;
        return this;
    }

    public Operation getOperation() {
        return operation;
    }

    public FederatedOperation ids(final String... ids) {
        return ids(Sets.newLinkedHashSet(Arrays.asList(ids)));
    }

    public Set<String> getIds() {
        return ids;
    }

    public FederatedOperation mergeOperation(final KorypheBinaryOperator mergeOperation) {
        this.mergeOperation = mergeOperation;
        return this;
    }

    public KorypheBinaryOperator getMergeOperation() {
        return mergeOperation;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FederatedOperation that = (FederatedOperation) o;

        return new EqualsBuilder()
                .append(operation, that.operation)
                .append(ids, that.ids)
                .append(options, that.options)
                .append(mergeOperation, that.mergeOperation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(operation)
                .append(ids)
                .append(options)
                .append(mergeOperation)
                .toHashCode();
    }
}
