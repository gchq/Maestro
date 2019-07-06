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

import com.google.common.collect.Iterables;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.handler.OutputOperationHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A {@code ToArrayHandler} handles ToArray operations. The input {@link Iterable}
 * of objects is converted into an array.
 * <p>
 * Use of this operation will cause all of the items present in the input iterable
 * to be brought into memory, so this operation is not suitable for situations where
 * the size of the input iterable is very large.
 *
 * @param <T> the type of object in the input iterable
 */
public class ToArrayHandler<T> implements OutputOperationHandler<T[]> {
    @SuppressFBWarnings(value = "PZLA_PREFER_ZERO_LENGTH_ARRAYS")
    @Override
    public T[] _doOperation(final Operation /*ToArray<T>*/ operation, final Context context,
                            final Executor executor) throws OperationException {
        if (null == operation.input() || Iterables.isEmpty((Iterable<T>) operation.input())) {
            return null;
        }

        final Set<Class> classes = new HashSet<>();
        final Collection<T> collection;
        if (operation.input() instanceof Collection) {
            collection = (Collection) operation.input();
            collection.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> classes.add(e.getClass()));

        } else {
            collection = new ArrayList<>();
            for (final T t : (Iterable<T>) operation.input()) {
                if (null != t) {
                    classes.add(t.getClass());
                }
                collection.add(t);
            }
        }

        if (classes.isEmpty()) {
            // If we return an empty Object array then we will get a class cast exception
            // when it is casted into T[].
            return null;
        }

        // Attempt to find a single common super class for the array.
        final Class clazz;
        if (1 == classes.size()) {
            clazz = classes.iterator().next();
        } else {
            // This may cause class cast exceptions.
            clazz = Object.class;
        }

        return collection.toArray((T[]) Array.newInstance(clazz, collection.size()));
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration(this.getClass())
                .field("input", Iterable.class);
    }


}
