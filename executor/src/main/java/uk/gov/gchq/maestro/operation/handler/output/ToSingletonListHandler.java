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

package uk.gov.gchq.maestro.operation.handler.output;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.fields.FieldsUtil;
import uk.gov.gchq.maestro.operation.handler.OutputOperationHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ToSingletonListHandler<T> implements OutputOperationHandler<List<? extends T>> {
    @Override
    public List<? extends T> doOperation(final Operation /*ToSingletonList<T>*/ operation,
                                         final Context context,
                                         final Executor executor) throws OperationException {
        Arrays.stream(Fields.values()).forEach(f -> f.validate(operation));

        if (null != operation.input()) {
            return Collections.singletonList((T) Fields.Input.get(operation));
        } else {
            throw new OperationException("Input cannot be null");
        }
    }


    public enum Fields {
        Input;

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
