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
package uk.gov.gchq.maestro.executor.operation.validator;

import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.operation.Operation;

/**
 * The OperationValidation interface if implemented will get the
 * operationValidator from the Store and validate each Operation as its run.
 *
 */
public interface OperationValidation {

    default Operation prepareOperation(final Operation operation, final Context context,
                                       final Executor executor) {
        final OperationValidator opValidator = new OperationValidator();
        final ValidationResult validationResult =
                opValidator.validate(operation, context.getUser(), executor);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Operation is invalid. " + validationResult
                    .getErrorString());
        }
        return operation;
    }
}
