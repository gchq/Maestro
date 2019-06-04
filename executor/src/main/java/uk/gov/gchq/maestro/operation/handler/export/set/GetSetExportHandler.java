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
import uk.gov.gchq.maestro.commonutil.exception.MaestroRuntimeException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.fields.FieldsUtil;
import uk.gov.gchq.maestro.operation.handler.export.GetExportHandler;

import java.util.Arrays;

/**
 * Implementation of the {@link GetExportHandler} to retrieve exported created by a SetExporter.
 */
public class GetSetExportHandler extends GetExportHandler {
    @Override
    protected CloseableIterable<?> getExport(final Operation export, final Operation exporter) throws OperationException {
        Arrays.stream(Fields.values()).forEach(f -> f.validate(export));
        final String o = (String) Fields.KeyOrDefault.get(export);
        final int o1 = (int) Fields.Start.get(export);
        final Integer o2 = (Integer) Fields.End.get(export);
        throw new MaestroRuntimeException("examine SetExporterTest not finished implemented exporter.get(o, o1, o2)");
        // return (CloseableIterable<?>) exporter.get(o, o1, o2); TODO examine SetExporterTest not finished implemented
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

    public enum Fields {
        Start(Integer.class),
        End(Integer.class),
        KeyOrDefault(String.class);

        Class instanceOf;

        Fields() {
            this(Object.class);
        }

        Fields(final Class instanceOf) {
            this.instanceOf = instanceOf;
        }

        public void validate(Operation operation) {
            FieldsUtil.validate(this, operation, instanceOf);
        }

        public Object get(Operation operation) {

            return FieldsUtil.get(operation, this);
        }
    }

}
