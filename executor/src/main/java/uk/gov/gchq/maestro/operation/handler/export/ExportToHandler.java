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
import uk.gov.gchq.maestro.commonutil.exception.MaestroRuntimeException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;

import java.util.Arrays;
import java.util.Collections;

/**
 * Abstract class describing how to handle  ExportTo operations.
 */
public abstract class ExportToHandler extends ExportOperationHandler {
    @Override
    public Object doOperation(final Operation operation,
                              final Context context,
                              final Executor executor,
                              final Operation exporter)
            throws OperationException {
        final Iterable<?> inputItr = wrapInIterable(operation.input());
        throw new MaestroRuntimeException("Not yet implemented");
        // TODO
        // exporter.add((String) operation.get("KeyOrDefault"), inputItr);
        // return operation.input();
    }

    private Iterable<?> wrapInIterable(final Object input) {
        if (null == input) {
            return Collections.emptyList();
        }

        final Iterable inputItr;
        if (input instanceof Iterable) {
            inputItr = (Iterable) input;
        } else if (input.getClass().isArray()) {
            inputItr = Arrays.asList((Object[]) input);
        } else {
            inputItr = Collections.singleton(input);
        }
        return inputItr;
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration(this.getClass())
                .field("input", Object.class);
    }
}
