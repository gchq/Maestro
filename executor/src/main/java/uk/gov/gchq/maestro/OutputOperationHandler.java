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

package uk.gov.gchq.maestro;

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.io.Output;

/**
 * An {@code OutputOperationHandler} defines how to handle a specific {@link Output} operations.
 */
public interface OutputOperationHandler<OP extends Output<O>, O> extends OperationHandler<OP> {
    /**
     * Execute the given {@link Output} operation.
     *
     * @param operation the {@link Output} operation to be executed
     * @param context   the operation chain context, containing the user who executed the operation
     * @param executor  the {@link Executor} the operation should be run on
     * @return the output for the operation.
     * @throws OperationException thrown if the operation fails
     */
    @Override
    O doOperation(final OP operation, final Context context,
                  final Executor executor) throws OperationException;
}
