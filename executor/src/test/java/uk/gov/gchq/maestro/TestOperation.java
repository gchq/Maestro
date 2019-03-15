package uk.gov.gchq.maestro;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TestOperation implements DoGetOperation<String> {
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
}
