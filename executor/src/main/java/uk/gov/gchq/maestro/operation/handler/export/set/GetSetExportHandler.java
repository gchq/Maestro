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

package uk.gov.gchq.maestro.operation.handler.export.set;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.operation.handler.export.GetExportHandler;
import uk.gov.gchq.maestro.operation.impl.export.set.GetSetExport;
import uk.gov.gchq.maestro.operation.impl.export.set.SetExporter;

/**
 * Implementation of the {@link GetExportHandler} to retrieve exported created by
 * a {@link SetExporter}.
 */
public class GetSetExportHandler extends GetExportHandler<GetSetExport, SetExporter> {
    @Override
    protected CloseableIterable<?> getExport(final GetSetExport export, final SetExporter exporter) throws OperationException {
        return exporter.get(export.getKeyOrDefault(), export.getStart(), export.getEnd());
    }

    @Override
    protected Class<SetExporter> getExporterClass() {
        return SetExporter.class;
    }

    @Override
    public SetExporter createExporter(final GetSetExport export,
                                      final Context context,
                                      final Executor executor) {
        return new SetExporter();
    }
}
