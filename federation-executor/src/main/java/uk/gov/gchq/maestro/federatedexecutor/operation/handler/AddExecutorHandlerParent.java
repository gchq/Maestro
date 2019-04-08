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

package uk.gov.gchq.maestro.federatedexecutor.operation.handler;


import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.federatedexecutor.operation.AddExecutor;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.FederatedUtil;

import java.util.Collections;


public abstract class AddExecutorHandlerParent<OP extends AddExecutor> implements OperationHandler<OP> {

    public static final String ERROR_BUILDING_GRAPH_GRAPH_ID_S = "Error building executor %s";
    public static final String ERROR_ADDING_GRAPH_GRAPH_ID_S = "Error adding executor %s";
    public static final String USER_IS_LIMITED_TO_ONLY_USING_PARENT_PROPERTIES_ID_FROM_GRAPHLIBRARY_BUT_FOUND_STORE_PROPERTIES_S = "User is limited to only using parentConfigId from the executorLibrary, but found storeProperties: %s";

    @Override
    public Void doOperation(final OP operation, final Context context, final Executor executor) throws OperationException {
        final User user = context.getUser();

        isLimitedToLibraryProperties(executor, user, operation);


        FederatedUtil.addExecutorTo(executor, operation);


//         final Executor executorSerialisable; TODO
//         try {
//             executorSerialisable = _makeGraph(operation, executor);
//         } catch (final Exception e) {
//             throw new OperationException(String.format(ERROR_BUILDING_GRAPH_GRAPH_ID_S, operation.getId()), e);
//         }
//
//         try {
//             executor.addGraphs(operation.getAuths(), context.getUser().getUserId(), operation.isPublic(), operation.isDisabledByDefault(), executorSerialisable);
// //        } catch (final StorageException e) {
// //            throw new OperationException(e.getMessage(), e);
//         } catch (final Exception e) {
//             throw new OperationException(String.format(ERROR_ADDING_GRAPH_GRAPH_ID_S, operation.getId()), e);
//         }
//
//         addGenericHandler(executor, executorSerialisable.getGraph());

        return null;
    }

    private void isLimitedToLibraryProperties(final Executor executor, final User user, final OP operation) throws OperationException {
        final String customPropertiesAuths = executor.getConfig().getProperties().get("customConfigAuths");
        final boolean isLimitedToLibraryProperties = null != customPropertiesAuths && Collections.disjoint(user.getOpAuths(), Sets.newHashSet(customPropertiesAuths.split(",")));
        if (isLimitedToLibraryProperties && null != operation.getConfig()/*TODO this logic is not correct*/) {
            throw new OperationException(String.format(USER_IS_LIMITED_TO_ONLY_USING_PARENT_PROPERTIES_ID_FROM_GRAPHLIBRARY_BUT_FOUND_STORE_PROPERTIES_S, operation.getConfig().toString()));
        }
    }

    // protected void addGenericHandler(final Executor executor) { TODO
    //     for (final Class<? extends Operation> supportedOperation : executor.getSupportedOperations()) {
    //         //some operations are not suitable for FederatedOperationGenericOutputHandler
    //         if (Output.class.isAssignableFrom(supportedOperation) && !executor.isSupported(supportedOperation)) {
    //             Class<? extends Output> supportedOutputOperation = (Class<? extends Output>) supportedOperation;
    //
    //             Class outputClass;
    //             try {
    //                 outputClass = supportedOutputOperation.newInstance().getOutputClass();
    //             } catch (final InstantiationException | IllegalAccessException e) {
    //                 continue;
    //             }
    //             if (CloseableIterable.class.equals(outputClass)) {
    //                 executor.addOperationHandler((Class) supportedOutputOperation, new FederatedOperationIterableHandler());
    //             }
    //         }
    //     }
    // }

    protected abstract Executor _makeExecutor(final OP operation, final Executor store);
}
