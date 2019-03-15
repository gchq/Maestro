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

import org.junit.Ignore;
import org.junit.Test;

import uk.gov.gchq.maestro.DoGetOperation;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.MaestroObjectTest;
import uk.gov.gchq.maestro.OperationHandler;
import uk.gov.gchq.maestro.exception.SerialisationException;

import java.util.Map;

public class FederationTest extends MaestroObjectTest {

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.Executor\",\n" +
                "  \"operationHandlerMap\" : { },\n" +
                "  \"config\" : { }\n" +
                "}";
    }

    @Override
    protected Executor getTestObject() {
        final Map<Class<? extends DoGetOperation>, OperationHandler> operationHandlerMap = null;
        final Map<String, String> config = null;

        final Executor executor = new Executor()
                .operationHandlerMap(operationHandlerMap)
                .config(config);

        return executor;
    }
}
