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

package uk.gov.gchq.maestro.operation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.maestro.helper.TestOperation;

import static org.junit.Assert.*;

public class DefaultOperationTest extends uk.gov.gchq.maestro.helper.MaestroObjectTest<DefaultOperation> {

    @Override
    protected Class<DefaultOperation> getTestObjectClass() {
        return DefaultOperation.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.DefaultOperation\",\n" +
                "  \"wrappedOp\" : {\n" +
                "    \"class\" : \"uk.gov.gchq.maestro.helper.TestOperation\"\n" +
                "  }\n" +
                "}";
    }

    @Override
    protected DefaultOperation getTestObject() throws Exception {
        return new DefaultOperation().setWrappedOp(new TestOperation());
    }

}