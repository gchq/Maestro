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
import uk.gov.gchq.maestro.operation.export.Exporter;
import uk.gov.gchq.maestro.operation.export.GetExport;

/**
 * Abstract class describing how to handle {@link GetExport} operations.
 *
 * @param <EXPORT>   the {@link GetExport} operation
 * @param <EXPORTER> the {@link Exporter} instance
 */
public abstract class GetExportHandler<EXPORT extends GetExport & Operation, EXPORTER extends Exporter> extends ExportOperationHandler<EXPORT, EXPORTER> {
    @Override
    public CloseableIterable<?> doOperation(final EXPORT operation,
                                            final Context context,
                                            final Executor executor,
                                            final EXPORTER exporter)
            throws OperationException {
        return getExport(operation, exporter);
    }

    protected CloseableIterable<?> getExport(final EXPORT export, final EXPORTER exporter) throws OperationException {
        return exporter.get(export.getKeyOrDefault());
    }
}
