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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.maestro.commonutil.Required;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.Map;



@JsonPropertyOrder(value = {"class", "graphId"}, alphabetic = true)
public class RemoveExecutor implements Operation {

    @Required
    private String graphId;
    protected Map<String, String> options;

    public String getGraphId() {
        return graphId;
    }

    public RemoveExecutor graphId(final String graphId) {
        this.graphId = graphId;
        return this;
    }

    @Override
    public RemoveExecutor shallowClone() throws CloneFailedException {
        return new RemoveExecutor()
                .graphId(graphId)
                .options(options);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final RemoveExecutor that = (RemoveExecutor) o;

        return new EqualsBuilder()
                .append(graphId, that.graphId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(graphId)
                .toHashCode();
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public RemoveExecutor options(final Map<String, String> options) {
        this.options = options;
        return (RemoveExecutor) this;
    }
}
