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

package uk.gov.gchq.maestro.operation.impl;

import org.junit.Test;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.helper.MaestroInitialiseHandlerBasicTest;
import uk.gov.gchq.maestro.operation.Operation;

import static org.junit.Assert.assertNotNull;

public class SingleProxyInitialiseHandlerTest extends MaestroInitialiseHandlerBasicTest<SingleProxyInitialiseHandler> {

    @Override
    protected SingleProxyInitialiseHandler getTestHandler() throws Exception {
        return new SingleProxyInitialiseHandler();
    }

    @Override
    protected Operation getBasicOp() {
        return new Operation(Executor.INITIALISER);
    }

    @Override
    protected Class<SingleProxyInitialiseHandler> getTestObjectClass() {
        return SingleProxyInitialiseHandler.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.impl.SingleProxyInitialiseHandler\"\n" +
                "}";
    }

    @Test
    public void shouldNotHaveNullFieldDeclarations() throws Exception {
        assertNotNull(getTestHandler().getFieldDeclaration());
    }
}
