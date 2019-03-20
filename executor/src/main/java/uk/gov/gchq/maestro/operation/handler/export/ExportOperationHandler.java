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
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationHandler;
import uk.gov.gchq.maestro.operation.export.Export;
import uk.gov.gchq.maestro.operation.export.Exporter;

/**
 * Abstract class describing how to handle {@link Export} operations.
 *
 * @param <EXPORT>   the {@link Export} operation
 * @param <EXPORTER> the {@link Exporter} instance
 */
public abstract class ExportOperationHandler<EXPORT extends Export & Operation, EXPORTER extends Exporter> implements OperationHandler<EXPORT> {
    @Override
    public Object doOperation(final EXPORT operation,
                              final Context context, final Executor executor)
            throws OperationException {
        EXPORTER exporter = context.getExporter(getExporterClass());
        if (null == exporter) {
            exporter = createExporter(operation, context, executor);
            if (null == exporter) {
                throw new OperationException("Unable to create exporter: " + getExporterClass());
            }
            context.addExporter(exporter);
        }

        return doOperation(operation, context, executor, exporter);
    }

    protected abstract Class<EXPORTER> getExporterClass();

    protected abstract EXPORTER createExporter(final EXPORT export,
                                               final Context context,
                                               final Executor executor);

    protected abstract Object doOperation(final EXPORT export,
                                          final Context context,
                                          final Executor executor,
                                          final EXPORTER exporter) throws OperationException;
}
