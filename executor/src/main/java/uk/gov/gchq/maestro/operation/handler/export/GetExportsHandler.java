/*
 * Copyright 2016-2019 Crown Copyright
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

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.OutputOperationHandler;
import uk.gov.gchq.maestro.operation.export.GetExport;
import uk.gov.gchq.maestro.operation.impl.export.GetExports;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handler for {@link GetExports} operations.
 */
public class GetExportsHandler implements OutputOperationHandler<GetExports, Map<String, CloseableIterable<?>>> {
    @Override
    public Map<String, CloseableIterable<?>> doOperation(final GetExports operation, final Context context, final Executor executor) throws OperationException {
        final Map<String, CloseableIterable<?>> exports = new LinkedHashMap<>();
        for (final GetExport getExport : operation.getGetExports()) {
            final CloseableIterable<?> export = executor.execute(new OperationChain((Operation) getExport), context);
            exports.put(getExport.getClass().getName() + ": " + getExport.getKeyOrDefault(), export);
        }

        return exports;
    }
}
