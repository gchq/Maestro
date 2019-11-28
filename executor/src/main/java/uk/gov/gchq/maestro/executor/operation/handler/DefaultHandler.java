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

package uk.gov.gchq.maestro.executor.operation.handler;

import uk.gov.gchq.maestro.commonutil.CloseableUtil;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.Objects;

import static java.lang.String.format;

public class DefaultHandler implements OperationHandler {

    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        CloseableUtil.close(operation);
        throw new OperationException(format("Operation %s is not explicitly supported by executor: %s. Operation: %s", operation.getId(), executor.getId(), operation));
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration();
    }

    @Override
    public boolean equals(final Object obj) {
        return Objects.nonNull(obj) && this.getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
