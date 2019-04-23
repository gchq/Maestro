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

package uk.gov.gchq.maestro.federatedexecutor.operation.handler.impl;


import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.federatedexecutor.operation.AddExecutor;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.io.Output;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.FederatedHandlersUtil;

import java.util.Collections;


public class AddExecutorHandler implements OperationHandler<AddExecutor> {

    public static final String ERROR_ADDING_EXECUTOR_ID_S_TO_S_S = "Error adding executorId: %s to: %s. -> %s";
    public static final String USER_IS_LIMITED_TO_ONLY_USING_PARENT_PROPERTIES_ID_FROM_GRAPHLIBRARY_BUT_FOUND_STORE_PROPERTIES_S = "User is limited to only using parentConfigId from the executorLibrary, but found storeProperties: %s";

    @Override
    public Void doOperation(final AddExecutor operation, final Context context, final Executor executor) throws OperationException {
        final User user = context.getUser();

        isLimitedToLibraryProperties(executor, user, operation);

        try {
            FederatedHandlersUtil.addExecutorTo(operation, executor, user.getUserId());
        } catch (final MaestroCheckedException e) {
            throw new OperationException(String.format(ERROR_ADDING_EXECUTOR_ID_S_TO_S_S, operation.getExecutor().getConfig().getId(), executor.getConfig().getId(), e.getMessage()), e);
        }

        addGenericHandler(executor);

        return null;
    }

    private void isLimitedToLibraryProperties(final Executor executor, final User user, final AddExecutor operation) throws OperationException {
        final String customPropertiesAuths = (String) executor.getConfig().getProperties().get("customConfigAuths");
        final boolean isLimitedToLibraryProperties = null != customPropertiesAuths && Collections.disjoint(user.getOpAuths(), Sets.newHashSet(customPropertiesAuths.split(",")));
        if (isLimitedToLibraryProperties && null != operation.getExecutor()/*TODO this logic is not correct*/) {
            throw new OperationException(String.format(USER_IS_LIMITED_TO_ONLY_USING_PARENT_PROPERTIES_ID_FROM_GRAPHLIBRARY_BUT_FOUND_STORE_PROPERTIES_S, operation.getExecutor().toString()));
        }
    }

    protected void addGenericHandler(final Executor executor) {
        //TODO this is Magic to aggregate chained iterables together.
        for (final Class<? extends Operation> supportedOperation : executor.getOperationHandlerMap().keySet()) {
            //some operations are not suitable for FederatedOperationGenericOutputHandler
            if (Output.class.isAssignableFrom(supportedOperation) && !executor.isSupported(supportedOperation)) {
                Class<? extends Output> supportedOutputOperation = (Class<? extends Output>) supportedOperation;

                Class outputClass;
                try {
                    outputClass = supportedOutputOperation.newInstance().getOutputClass();
                } catch (final InstantiationException | IllegalAccessException e) {
                    continue;
                }
                if (CloseableIterable.class.equals(outputClass)) {
                    //TODO re-add
                    // executor.getOperationHandlerMap().put(supportedOutputOperation, new FederatedOperationIterableHandler());
                }
            }
        }
    }

}
