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

package uk.gov.gchq.maestro.operation.handler.named;


import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.fields.FieldsUtil;
import uk.gov.gchq.maestro.operation.handler.OutputOperationHandler;

import java.util.Arrays;

/**
 * Operation handler for . Named operations are resolved by
 * the {@code NamedOperationResolver} {@code GraphHook}.
 * <p>
 * If this handler is invoked then it means the named operation could not be resolved.
 */
public class NamedOperationHandler implements OutputOperationHandler {
    @Override
    public Object doOperation(final Operation operation,
                              final Context context, final Executor executor) throws OperationException {
        Arrays.stream(Fields.values()).forEach(f -> f.validate(operation));
        throw new UnsupportedOperationException("The named operation: " + Fields.OperationName.get(operation) + " was not found.");
    }

    public enum Fields {
        OperationName(String.class);

        Class instanceOf;

        Fields() {
            this(Object.class);
        }

        Fields(final Class instanceOf) {
            this.instanceOf = instanceOf;
        }

        public void validate(Operation operation) {
            FieldsUtil.validate(this, operation, instanceOf);
        }

        public Object get(Operation operation) {
            return FieldsUtil.get(operation, this);
        }
    }
}
