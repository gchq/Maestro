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

package uk.gov.gchq.maestro.executor.operation.optimiser;

import uk.gov.gchq.maestro.operation.Operation;

/**
 * Optimises and applies preprocessing to operations.
 */
public interface OperationOptimiser {
    /**
     * Optimises the operation.
     * Operations can be swapped for
     * more efficient operations depending on the store impl.
     * The operation has already been cloned so changes may be made directly to the operation
     * parameter if required.
     * This method can be extended to add custom optimisation, but ensure super.optimise is called first.
     * Alternatively, the preferred approach is to override addPreOperations, optimiseCurrentOperation or addPostOperations
     *
     * @param operation the operation to optimise
     * @return the optimised operation
     */
    Operation optimise(final Operation operation);
}
