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

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.handler.OutputOperationHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for GetExports operations.
 */
public class GetExportsHandler implements OutputOperationHandler<Map<String, CloseableIterable<?>>> {
    @Override
    public Map<String, CloseableIterable<?>> doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        final Map<String, CloseableIterable<?>> exports = new LinkedHashMap<>();
        for (final Operation getExport : (List<Operation>) operation.get("GetExports")) {
            final CloseableIterable<?> export = executor.execute(new OperationChain(getExport.getId(), getExport), context);
            exports.put(getExport.getClass().getName() + ": " + getExport.get("KeyOrDefault"), export);
        }

        return exports;
    }
}
