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

package uk.gov.gchq.maestro.operation.impl.job;

import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationTest;
import uk.gov.gchq.maestro.operation.jobtracker.Repeat;

public class JobTest extends OperationTest {
    final String testJobId = "testId";
    final Operation inputOp = new Operation("CancelScheduledJob")
            .operationArg("jobId", testJobId);
    final Repeat repeat = new Repeat();

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "  \"id\" : \"Job\",\n" +
                "  \"operationArgs\" : {\n" +
                "    \"operation\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.operation.Operation\",\n" +
                "      \"id\" : \"CancelScheduledJob\",\n" +
                "      \"operationArgs\" : {\n" +
                "        \"jobId\" : \"testId\"\n" +
                "      }\n" +
                "    },\n" +
                "    \"repeat\" : {\n" +
                "      \"class\" : \"uk.gov.gchq.maestro.operation.jobtracker.Repeat\",\n" +
                "      \"initialDelay\" : 0,\n" +
                "      \"repeatPeriod\" : 0,\n" +
                "      \"timeUnit\" : \"SECONDS\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected Operation getFullyPopulatedTestObject() throws Exception {
        return new Operation("Job")
                .operationArg("operation", inputOp)
                .operationArg("repeat", repeat);
    }

}
