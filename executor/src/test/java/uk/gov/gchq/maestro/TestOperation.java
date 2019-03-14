package uk.gov.gchq.maestro;

public class TestOperation implements Operation {
    @Override
    public boolean equals(final Object o) {
        return getClass().equals(o.getClass());
    }
}
