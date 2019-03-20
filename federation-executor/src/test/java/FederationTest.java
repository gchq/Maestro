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

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.helpers.MaestroObjectTest;
import uk.gov.gchq.maestro.operation.OperationHandler;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.util.Config;

import java.util.Map;

public class FederationTest extends MaestroObjectTest<Executor> {

    @Override
    protected Class getTestObjectClass() {
        return Executor.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.Executor\",\n" +
                "  \"config\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.util.Config\",\n" +
                "    \"operationHandlers\" : { },\n" +
                "    \"hooks\" : [ ]\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Executor getTestObject() {
        final Map<Class<? extends Operation>, OperationHandler> operationHandlerMap = null;


        final Executor executor = new Executor()
                .operationHandlerMap(operationHandlerMap)
                .config(new Config());

        return executor;
    }
}
