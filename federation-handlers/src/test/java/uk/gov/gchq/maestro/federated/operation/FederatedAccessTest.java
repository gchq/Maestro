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

package uk.gov.gchq.maestro.federated.operation;

import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.federated.FederatedAccess;
import uk.gov.gchq.maestro.helper.MaestroObjectTest;

public class FederatedAccessTest extends MaestroObjectTest<FederatedAccess> {

    @Override
    protected Class<FederatedAccess> getTestObjectClass() {
        return FederatedAccess.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.federated.FederatedAccess\",\n" +
                "  \"addingUserId\" : \"userId\",\n" +
                "  \"graphAuths\" : [ \"A\", \"B\", \"C\" ],\n" +
                "  \"disabledByDefault\" : false,\n" +
                "  \"public\" : false\n" +
                "}";
    }

    @Override
    protected FederatedAccess getTestObject() throws Exception {
        return new FederatedAccess(Sets.newHashSet("A", "B", "C"), "userId");
    }
}
