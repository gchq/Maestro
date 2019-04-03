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
import uk.gov.gchq.maestro.operation.handler.export.ExportToHandler;
import uk.gov.gchq.maestro.operation.impl.export.set.ExportToSet;
import uk.gov.gchq.maestro.operation.impl.export.set.SetExporter;

/**
 * Implementation of the {@link ExportToHandler} abstract class to export objects
 * to a {@link java.util.Set} via a {@link SetExporter}.
 */
public class ExportToSetHandler extends ExportToHandler<ExportToSet, SetExporter> {
    @Override
    protected Class<SetExporter> getExporterClass() {
        return SetExporter.class;
    }

    @Override
    protected SetExporter createExporter(final ExportToSet export,
                                         final Context context,
                                         final Executor executor) {
        return new SetExporter();
    }
}
