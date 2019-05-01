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
import uk.gov.gchq.maestro.commonutil.stream.MaestroCollectors;
import uk.gov.gchq.maestro.commonutil.stream.Streams;
import uk.gov.gchq.maestro.operation.handler.OutputOperationHandler;
import uk.gov.gchq.maestro.operation.impl.output.ToSet;

import java.util.Set;

/**
 * The {@code ToSetHandler} handles {@link ToSet} operations by collecting the
 * items in the input {@link Iterable} into a {@link Set}, removing duplicate items
 * in the process.
 * <p>
 * Use of this operation will cause all of the items present in the input iterable
 * to be brought into memory, so this operation is not suitable for situations where
 * the size of the input iterable is very large.
 *
 * @param <T> the type of object contained in the input iterable
 */
public class ToSetHandler<T> implements OutputOperationHandler<ToSet<T>, Set<? extends T>> {
    @Override
    public Set<T> doOperation(final ToSet<T> operation, final Context context, final Executor executor) throws OperationException {
        if (null == operation.getInput()) {
            return null;
        }

        return Streams.toStream(operation.getInput())
                .collect(MaestroCollectors.toLinkedHashSet());
    }
}