package uk.gov.gchq.maestro;

import java.util.HashMap;
import java.util.Map;


public class ExecutorTest extends MaestroObjectTest {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.Executor\",\n" +
                "  \"operationHandlerMap\" : {\n" +
                "    \"uk.gov.gchq.maestro.TestOperation\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.TestHandler\",\n" +
                "      \"field\" : \"fieldValue1\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"config\" : {\n" +
                "    \"configKey\" : \"configValue\"\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Executor getTestObject() {
        Map<Class<? extends Operation>, OperationHandler> operationHandlerMap = new HashMap<>();
        operationHandlerMap.put(TestOperation.class, new TestHandler().field("fieldValue1"));
        final Map<String, String> config = new HashMap<>();
        config.put("configKey", "configValue");
        return new Executor().operationHandlerMap(operationHandlerMap).config(config);
    }

}
