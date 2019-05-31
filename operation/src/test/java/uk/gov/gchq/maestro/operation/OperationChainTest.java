package uk.gov.gchq.maestro.operation;

import static org.junit.Assert.*;

public class OperationChainTest extends OperationTest {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.OperationChain\",\n" +
                "  \"id\" : \"testChain\",\n" +
                "  \"operations\" : [ {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "    \"id\" : \"innerOp1\",\n" +
                "    \"operationArgs\" : { }\n" +
                "  }, {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "    \"id\" : \"innerOp2\",\n" +
                "    \"operationArgs\" : { }\n" +
                "  } ],\n" +
                "  \"operationArgs\" : { }\n" +
                "}";
    }

    @Override
    protected Operation getFullyPopulatedTestObject() throws Exception {
        return new OperationChain("testChain", new Operation("innerOp1"), new Operation("innerOp2"));
    }
}