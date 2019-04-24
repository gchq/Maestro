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

package uk.gov.gchq.maestro.federatedexecutor.operation;

import uk.gov.gchq.koryphe.impl.binaryoperator.Max;
import uk.gov.gchq.maestro.helper.MaestroObjectTest;
import uk.gov.gchq.maestro.operation.Operation;

public class FederatedOperationTest extends MaestroObjectTest<FederatedOperation> {

    @Override
    protected Class<FederatedOperation> getTestObjectClass() {
        return FederatedOperation.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.federatedexecutor.operation.FederatedOperation\",\n" +
                "  \"ids\" : [ \"a\", \"b\", \"c\" ],\n" +
                "  \"mergeOperation\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.koryphe.impl.binaryoperator.Max\"\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected FederatedOperation getTestObject() throws Exception {
        return new FederatedOperation().operation((Operation) null).ids("a", "b", "c").mergeOperation(new Max());
    }
}
