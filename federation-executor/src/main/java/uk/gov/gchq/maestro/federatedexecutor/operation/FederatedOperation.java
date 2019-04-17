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

import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;

import java.util.Map;

public abstract class FederatedOperation<T extends FederatedOperation> implements Operation {

    protected Map<String, String> options;

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public T options(final Map<String, String> options) {
        this.options = options;
        return (T) this;
    }

    public static boolean hasFederatedOperations(final OperationChain<?> operationChain) {
        //TODO WHAT WHT? FederatedOperation?
        for (final Operation operation : operationChain.getOperations()) {
            if (operation instanceof FederatedOperation) {
                return true;
            }
        }

        return false;
    }
}
