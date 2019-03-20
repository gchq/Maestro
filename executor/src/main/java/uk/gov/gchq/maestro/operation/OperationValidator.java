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

import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.user.User;

/**
 * Validation class for validating {@link Operation}s
 */
public class OperationValidator {

    public ValidationResult validate(final Operation operation, final User user,
                                     final Executor executor) {
        return new ValidationResult();
    }
}
