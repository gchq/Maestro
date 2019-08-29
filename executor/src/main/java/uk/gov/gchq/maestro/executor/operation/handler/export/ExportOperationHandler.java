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

package uk.gov.gchq.maestro.executor.operation.handler.export;

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.Operation;

/**
 * Abstract class describing how to handle  Export operations.
 *
 */
public abstract class ExportOperationHandler implements OperationHandler {
    @Override
    public Object _doOperation(final Operation operation,
                              final Context context, final Executor executor)
            throws OperationException {
        Operation exporter = context.getExporter(getExporterId());
        if (null == exporter) {
            exporter = createExporter(operation, context, executor);
            if (null == exporter) {
                throw new OperationException("Unable to create exporter: " + getExporterId());
            }
            context.addExporter(exporter);
        }

        return doOperation(operation, context, executor, exporter);
    }

    protected abstract String getExporterId();

    protected abstract Operation createExporter(final Operation export,
                                               final Context context,
                                               final Executor executor);

    protected abstract Object doOperation(final Operation export,
                                          final Context context,
                                          final Executor executor,
                                          final Operation exporter) throws OperationException;
}
