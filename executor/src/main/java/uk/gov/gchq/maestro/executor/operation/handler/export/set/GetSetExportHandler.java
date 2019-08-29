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

package uk.gov.gchq.maestro.executor.operation.handler.export.set;

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.commonutil.iterable.LimitedCloseableIterable;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.executor.operation.handler.export.GetExportHandler;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the {@link GetExportHandler} to retrieve exported created by a SetExporter.
 */
public class GetSetExportHandler extends GetExportHandler {
    @Override
    protected CloseableIterable<?> getExport(final Operation export, final Operation exporter) throws OperationException {
        final String key = (String) export.get("KeyOrDefault");
        final int start = (int) export.get("Start");
        final Integer end = (Integer) export.get("End");
        final Map<String, Set<Object>> exports = (Map<String, Set<Object>>) export.getOrDefault("Exports", new HashMap<String, Set<Object>>());
        final CloseableIterable<?> rtn = get(key, start, end, exports);
        return rtn;
    }

    public CloseableIterable<?> get(final String key, final int start, final Integer end, final Map<String, Set<Object>> exports) {
        return new LimitedCloseableIterable<>(getExport(key, exports), start, end);
    }

    private Set<Object> getExport(final String key, final Map<String, Set<Object>> exports) {
        Set<Object> export = exports.computeIfAbsent(key, k -> new LinkedHashSet<>());

        return export;
    }

    protected String getExporterId() {
        return "SetExporter";
    }

    @Override
    public Operation createExporter(final Operation export,
                                    final Context context,
                                    final Executor executor) {
        return new Operation("SetExporter");
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration()
                .field("Start", Integer.class)
                .field("End", Integer.class)
                .field("KeyOrDefault", String.class)
                .fieldOptional("Exports", HashMap.class);
    }
}
