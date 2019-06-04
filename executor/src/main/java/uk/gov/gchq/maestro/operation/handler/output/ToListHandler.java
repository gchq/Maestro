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
import uk.gov.gchq.maestro.commonutil.stream.Streams;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.fields.FieldsUtil;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code ToListHandler} handles  ToList operations by collecting the
 * items in the input {@link Iterable} into a {@link List}.
 * <p>
 * Use of this operation will cause all of the items present in the input iterable
 * to be brought into memory, so this operation is not suitable for situations where
 * the size of the input iterable is very large.
 *
 * @param <T> the type of object contained in the input iterable
 */
public class ToListHandler<T> implements OperationHandler {
    @Override
    public List<T> doOperation(final Operation operation,
                               final Context context, final Executor executor) throws OperationException {
        Arrays.stream(Fields.values()).forEach(f -> f.validate(operation));


        final Object input = operation.input();
        if (null == input) {
            return null;
        }

        return Streams.toStream((Iterable<T>) input)
                .collect(Collectors.toList());
    }

    public enum Fields {
        input(Iterable.class);

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
