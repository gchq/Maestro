package uk.gov.gchq.maestro.operation;

public class OperationChainWrapTest extends OperationTest {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.OperationChain\",\n" +
                "  \"id\" : \"chainWrapChain\",\n" +
                "  \"operations\" : [ {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "    \"id\" : \"innerOperation\",\n" +
                "    \"operationArgs\" : {\n" +
                "      \"input\" : [ \"[Ljava.lang.Object;\", [ \"value1\", \"value2\" ] ]\n" + //TODO Ljava
                "    }\n" +
                "  } ],\n" +
                "  \"operationArgs\" : { }\n" +
                "}";
    }

    @Override
    protected Operation getFullyPopulatedTestObject() throws Exception {
        return OperationChain.wrap("chainWrap",
                new Operation("innerOperation")
                        .input(new Object[]{"value1", "value2"})
        );
    }
}