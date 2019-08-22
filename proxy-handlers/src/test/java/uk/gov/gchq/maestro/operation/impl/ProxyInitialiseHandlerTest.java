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

import uk.gov.gchq.maestro.helper.MaestroHandlerBasicTest;
import uk.gov.gchq.maestro.operation.Operation;

import static org.junit.Assert.*;

public class ProxyInitialiseHandlerTest extends MaestroHandlerBasicTest<ProxyInitialiseHandler> {

    @Override
    protected ProxyInitialiseHandler getTestHandler() throws Exception {
        return new ProxyInitialiseHandler();
    }

    @Override
    protected Operation getBasicOp() throws Exception {
        return new Operation("test");
    }

    @Override
    protected Class<ProxyInitialiseHandler> getTestObjectClass() {
        return ProxyInitialiseHandler.class;
    }

    @Override
    protected String getJSONString() {
        return "{\n" +
                "  \"class\" : \"uk.gov.gchq.maestro.operation.impl.ProxyInitialiseHandler\"\n" +
                "}";
    }

    @Test
    public void shouldNotHaveNullFieldDeclarations() throws Exception {
        assertNotNull(getTestHandler().getFieldDeclaration());
    }
}