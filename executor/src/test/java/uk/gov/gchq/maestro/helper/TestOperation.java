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

package uk.gov.gchq.maestro.helper;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.maestro.operation.Operation;

import java.util.Map;

public class TestOperation implements Operation {
    private String field;
    private Map<String, String> options;

    public String getField() {
        return field;
    }

    public TestOperation setField(final String field) {
        this.field = field;
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

        final TestOperation that = (TestOperation) o;

        return new EqualsBuilder()
                .append(field, that.field)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(field)
                .toHashCode();
    }

    @Override
    public Operation shallowClone() throws CloneFailedException {
        final TestOperation testOperation = new TestOperation();
        testOperation.setField(field);
        testOperation.options(options);
        return testOperation;
    }

    @Override
    public Map<String, String> getOptions() {
        return this.options;
    }

    @Override
    public Operation options(final Map<String, String> options) {
        this.options = options;
        return this;
    }
}
