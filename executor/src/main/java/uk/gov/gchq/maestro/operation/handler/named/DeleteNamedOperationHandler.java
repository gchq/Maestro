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
package uk.gov.gchq.maestro.operation.handler.named;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.handler.named.cache.NamedOperationCache;
import uk.gov.gchq.maestro.util.ExecutorPropertiesUtil;

/**
 * Operation Handler for DeleteNamedOperation.
 */
public class DeleteNamedOperationHandler implements OperationHandler {
    private final NamedOperationCache cache;

    public DeleteNamedOperationHandler() {
        this(new NamedOperationCache());
    }

    public DeleteNamedOperationHandler(final NamedOperationCache cache) {
        this.cache = cache;
    }

    /**
     * Deletes a NamedOperation from the cache specified in the Operations Declarations file (assuming the user has
     * write privileges on the specified NamedOperation). The user needs only to provide the name of the operation they
     * want to delete.
     *
     * @param operation the {@link uk.gov.gchq.maestro.operation.Operation} to be
     *                  executed
     * @param context   the operation chain context, containing the user who executed the operation
     * @param executor  the {@link Executor} the operation should be run on
     * @return null (as output of this operation is void)
     * @throws OperationException thrown if the user doesn't have permission to delete the NamedOperation
     */
    @Override
    public Void _doOperation(final Operation operation,
                             final Context context, final Executor executor) throws OperationException {
        try {

            cache.deleteNamedOperation((String) operation.get("OperationName"),
                    context.getUser(),
                    ExecutorPropertiesUtil.getAdminAuth(executor.getConfig().getProperties()));
        } catch (final MaestroCheckedException e) {
            throw new OperationException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration(this.getClass()).field("OperationName", String.class);
    }
}
