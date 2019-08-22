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

import uk.gov.gchq.maestro.commonutil.exception.MaestroRuntimeException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.rest.factory.ExecutorFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of the {@link uk.gov.gchq.maestro.rest.service.v2.example.ExamplesFactory}
 * interface. Required to be registered with HK2 to allow the correct {@link ExecutorFactory} object to be injected.
 */
public class DefaultExamplesFactory implements ExamplesFactory {
    public static final String EXAMPLE_OP_CHAIN = "exampleOpChain";
    @Inject
    private ExecutorFactory executorFactory;

    private Map<String, Operation> examplesMap;

    public DefaultExamplesFactory() {
        // public constructor required by HK2
    }

    @PostConstruct
    public void generateExamples() {
        final Map<String, Operation> rtn = new HashMap<>();
        final OperationChain exampleOpChain = operationChain();
        rtn.put(exampleOpChain.getId(), exampleOpChain);

        examplesMap = rtn;
    }

    @Override
    public Operation generateExample(final String operationType) {
        if (null == examplesMap) {
            generateExamples();
        }

        if (examplesMap.containsKey(operationType)) {
            return examplesMap.get(operationType);
        } else {
            throw new MaestroRuntimeException(new UnsupportedOperationException("generateExamples does not support operation of type: " + operationType));
        }
    }

    public OperationChain operationChain() {
        return new OperationChain(EXAMPLE_OP_CHAIN);
    }

}
