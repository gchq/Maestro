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

package uk.gov.gchq.maestro.rest.service.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.commonutil.CloseableUtil;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.OperationChainDAO;
import uk.gov.gchq.maestro.rest.factory.ExecutorFactory;
import uk.gov.gchq.maestro.rest.factory.UserFactory;

import javax.inject.Inject;

/**
 * An implementation of {@link IOperationService}.
 * All operations are delegated to the Executor.
 * Pre and post operation hooks are available by extending this class and implementing preOperationHook and/or
 * postOperationHook.
 */
public class OperationService implements IOperationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationService.class);

    @Inject
    private ExecutorFactory executorFactory;

    @Inject
    private UserFactory userFactory;

    public OperationService() {

    }

    @Override
    public Object execute(final Operation operation) {
        return _execute(operation);
    }

    protected void preOperationHook(final OperationChain<?> opChain, final Context context) {
        // no action by default
    }

    protected void postOperationHook(final OperationChain<?> opChain, final Context context) {
        // no action by default
    }

    protected <O> O _execute(final Operation operation) {
        return _execute(new OperationChainDAO<>(operation));
    }

    @SuppressWarnings("ThrowFromFinallyBlock")
    protected <O> O _execute(final OperationChainDAO<O> opChain) {
        final Context context = userFactory.createContext();
        preOperationHook(opChain, context);

        O result;
        try {
            result = executorFactory.getExecutor().execute(opChain, context);
        } catch (final OperationException e) {
            CloseableUtil.close(opChain);
            throw new RuntimeException("Error executing operation chain: " + e.getMessage(), e);
        } finally {
            try {
                postOperationHook(opChain, context);
            } catch (final Exception e) {
                CloseableUtil.close(opChain);
                throw e;
            }
        }

        return result;
    }
}
