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
package uk.gov.gchq.maestro.executor.operation.handler.output;

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.stream.Streams;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.Operation;

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
    public List<T> _doOperation(final Operation operation,
                               final Context context, final Executor executor) throws OperationException {
        final Object input = operation.input();
        if (null == input) {
            return null;
        }

        return Streams.toStream((Iterable<T>) input)
                .collect(Collectors.toList());
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration().field("input", Iterable.class);
    }

}
