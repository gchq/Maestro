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

package uk.gov.gchq.maestro.rest.service.v2.example;

import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;

import javax.inject.Inject;

public class ExamplesServiceV2 implements IExamplesServiceV2 {

    @Inject
    private ExamplesFactory examplesFactory;

    @Override
    public Operation execute() throws InstantiationException, IllegalAccessException {
        final Operation testExample = examplesFactory.generateExample("testExample");
        return new OperationChain(testExample.getId(), testExample);
    }

    @Override
    public Operation executeChunked() throws InstantiationException, IllegalAccessException {
        final Operation testExample = examplesFactory.generateExample("testExample");
        return new OperationChain(testExample.getId(), testExample);
    }

}
