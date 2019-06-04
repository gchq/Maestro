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
import uk.gov.gchq.maestro.operation.fields.FieldsUtil;

import java.util.Arrays;

/**
 * Abstract class describing how to handle GetExport operations.
 */
public abstract class GetExportHandler extends ExportOperationHandler {
    @Override
    public CloseableIterable<?> doOperation(final Operation operation,
                                            final Context context,
                                            final Executor executor,
                                            final Operation exporter)
            throws OperationException {
        Arrays.stream(Fields.values()).forEach(f -> f.validate(operation));
        return getExport(operation, exporter);
    }

    protected CloseableIterable<?> getExport(final Operation export, final Operation exporter) throws OperationException {
        final Object o = Fields.KeyOrDefault.get(export);
        return (CloseableIterable<?>) exporter.get((String) o);
    }

    public enum Fields {
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
