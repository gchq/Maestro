package uk.gov.gchq.maestro;


public class TestHandler implements OperationHandler<TestOperation> {

    private String handlerField;

    @Override
    public boolean equals(final Object o) {
        return getClass().equals(o.getClass());
    }

    public String getHandlerField() {
        return handlerField;
    }

    public TestHandler fieldHandler(final String field) {
        this.handlerField = field;
        return this;
    }

    @Override
    public String doOperation(final TestOperation operation, final Context context, final Executor executor) {
        return handlerField + "," + operation.getField();
    }
}
