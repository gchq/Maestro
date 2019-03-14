package uk.gov.gchq.maestro;

public class OperationDeclaration {
    public Class<? extends Operation> getOperation() {
        throw new UnsupportedOperationException();
    }

    public OperationHandler getHandler() {
        throw new UnsupportedOperationException();
    }
}
