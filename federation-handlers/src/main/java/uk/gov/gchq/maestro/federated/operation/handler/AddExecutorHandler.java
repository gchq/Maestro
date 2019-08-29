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

package uk.gov.gchq.maestro.federated.operation.handler;


import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.federated.util.FederatedHandlersUtil;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.user.User;

import java.util.Collections;
import java.util.Set;

import static java.util.Objects.nonNull;


public class AddExecutorHandler implements OperationHandler {

    public static final String ERROR_ADDING_EXECUTOR_ID_S_TO_S_S = "Error adding executorId: %s to: %s. -> %s";
    public static final String USER_IS_LIMITED_TO_ONLY_USING_PARENT_PROPERTIES_ID_FROM_GRAPHLIBRARY_BUT_FOUND_STORE_PROPERTIES_S = "User is limited to only using parentConfigId from the executorLibrary, but found storeProperties: %s";
    public static final String CONFIG = "config";
    public static final String IS_PUBLIC = "isPublic";
    public static final String DISABLED_BY_DEFAULT = "disabledByDefault";
    public static final String AUTHS = "auths";
    public static final String CUSTOM_CONFIG_AUTHS = "customConfigAuths";
    public static final String PARENT_CONFIG_ID = "parentConfigId";

    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        try {
            final User user = context.getUser();
            processLimitedToLibraryConfig(operation, executor, user);
            FederatedHandlersUtil.addExecutorTo(operation, executor, user.getUserId());
        } catch (final Exception e) {
            throw new OperationException(String.format(ERROR_ADDING_EXECUTOR_ID_S_TO_S_S, ((Config) operation.get(CONFIG)).getId(), executor.getId(), e.getMessage()), e);
        }

        addGenericHandler(executor);

        return null;
    }

    public void processLimitedToLibraryConfig(final Operation operation, final Executor executor, final User user) throws OperationException {
        /*
         * TODO SHOULD THIS BE HOOK?
         */
        final boolean limitedToLibraryConfig = isUserLimitedToLibraryConfig(executor.getProperty(CUSTOM_CONFIG_AUTHS), user.getOpAuths());
        final boolean hasConfig = nonNull(operation.get(CONFIG));
        if (limitedToLibraryConfig && hasConfig) {
            throw new OperationException(String.format(USER_IS_LIMITED_TO_ONLY_USING_PARENT_PROPERTIES_ID_FROM_GRAPHLIBRARY_BUT_FOUND_STORE_PROPERTIES_S, operation.getExecutor().toString()));
        }
    }

    private boolean isUserLimitedToLibraryConfig(final String executorAuthsForCustomProperties, final Set<String> userOpAuths) {
        return nonNull(executorAuthsForCustomProperties)
                && Collections.disjoint(userOpAuths, Sets.newHashSet(executorAuthsForCustomProperties.split(",")));

    }

    protected void addGenericHandler(final Executor executor) {
        //TODO this is Magic to aggregate chained iterables together.
        for (final String operation : executor.getOperationHandlerMap().keySet()) {
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

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration()
                .field(CONFIG, Config.class)
                .fieldOptional(IS_PUBLIC, Boolean.class)
                .fieldOptional(DISABLED_BY_DEFAULT, Boolean.class)
                .fieldOptional(AUTHS, Set.class)
                .fieldOptional(PARENT_CONFIG_ID, String.class);
    }
}
