package uk.gov.gchq.maestro;


public class TestHandler implements OperationHandler<TestOperation> {

    private String field;

    @Override
    public boolean equals(final Object o) {
        return getClass().equals(o.getClass());
    }

    public String getField() {
        return field;
    }

    public TestHandler field(final String field) {
        this.field = field;
        return this;
    }
}
