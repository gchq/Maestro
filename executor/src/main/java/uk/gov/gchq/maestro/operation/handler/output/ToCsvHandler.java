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
package uk.gov.gchq.maestro.operation.handler.output;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.data.generator.StringGenerator;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclaration;
import uk.gov.gchq.maestro.operation.handler.OutputOperationHandler;

/**
 * A {@code ToCsvHandler} handles ToCsv operations by applying the provided
 * {@link uk.gov.gchq.maestro.data.generator.StringGenerator} to each item in the
 * input {@link Iterable}.
 */
public class ToCsvHandler<T> implements OutputOperationHandler<Iterable<? extends String>> {
    @Override
    public Iterable<? extends String> _doOperation(final Operation operation,
                                                   final Context context,
                                                   final Executor executor) throws OperationException {
        if (null == operation.input()) {
            return null;
        }

        final uk.gov.gchq.maestro.data.generator.StringGenerator generator = (StringGenerator) operation.get("ElementGenerator");
        if (null == generator) {
            throw new IllegalArgumentException("ToCsv operation requires a generator");
        }

        final Iterable<? extends String> csv = generator.apply((Iterable) operation.input());

        return csv;
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration(this.getClass()).field("ElementGenerator", StringGenerator.class);
    }

}
