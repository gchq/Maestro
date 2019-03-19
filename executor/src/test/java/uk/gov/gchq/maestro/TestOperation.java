package uk.gov.gchq.maestro;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.maestro.operation.Operation;

import java.util.Map;

public class TestOperation implements Operation {
    private String field;

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
        return null;
    }

    @Override
    public Map<String, String> getOptions() {
        return null;
    }

    @Override
    public void setOptions(final Map<String, String> options) {

    }
}
