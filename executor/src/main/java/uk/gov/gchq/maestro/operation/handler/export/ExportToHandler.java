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

package uk.gov.gchq.maestro.operation.handler.export;

import com.google.common.collect.Iterables;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract class describing how to handle  ExportTo operations.
 */
public abstract class ExportToHandler extends ExportOperationHandler {
    @Override
    public Object doOperation(final Operation operation,
                              final Context context,
                              final Executor executor,
                              final Operation exporter)
            throws OperationException {
        final Iterable<?> inputItr = wrapInIterable(operation.input());
        final Map<String, Set<Object>> exports = (Map<String, Set<Object>>) operation.getOrDefault("Exports", new HashMap<String, Set<Object>>());
        add((String) operation.get("KeyOrDefault"), inputItr, exports);
        return operation.input();
    }

    public void add(final String key, final Iterable<?> results, final Map<String, Set<Object>> exports) {
        Iterables.addAll(getExport(key, exports), results);
    }

    private Set<Object> getExport(final String key, final Map<String, Set<Object>> exports) {
        Set<Object> export = exports.computeIfAbsent(key, k -> new LinkedHashSet<>());

        return export;
    }

    private Iterable<?> wrapInIterable(final Object input) {
        if (null == input) {
            return Collections.emptyList();
        }

        final Iterable inputItr;
        if (input instanceof Iterable) {
            inputItr = (Iterable) input;
        } else if (input.getClass().isArray()) {
            inputItr = Arrays.asList((Object[]) input);
        } else {
            inputItr = Collections.singleton(input);
        }
        return inputItr;
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration()
                .field("input", Object.class);
    }
}
