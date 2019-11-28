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

package uk.gov.gchq.maestro.executor.operation.handler.output;

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.executor.operation.handler.OutputOperationHandler;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.Collections;
import java.util.List;

public class ToSingletonListHandler<T> implements OutputOperationHandler<List<? extends T>> {
    @Override
    public List<? extends T> _doOperation(final Operation /*ToSingletonList<T>*/ operation,
                                          final Context context,
                                          final Executor executor) throws OperationException {
        if (null != operation.input()) {
            return Collections.singletonList((T) operation.input());
        } else {
            throw new OperationException("Input cannot be null");
        }
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration().field("Input", Object.class);
    }
}
