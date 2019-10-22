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

package uk.gov.gchq.maestro.federated.hook;

import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.executor.hook.Hook;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.executor.util.Config;
import uk.gov.gchq.maestro.executor.util.Request;
import uk.gov.gchq.maestro.federated.handler.AddExecutorHandler;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.Operations;
import uk.gov.gchq.maestro.operation.user.User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.nonNull;

public class LimitUserToConfigLibrary implements Hook {
    public static final String USER_IS_LIMITED_TO_ONLY_USING_PARENT_PROPERTIES_ID_FROM_GRAPHLIBRARY_BUT_FOUND_STORE_PROPERTIES_S = "User is limited to only using parentConfigId from the executorLibrary, but found Properties: %s";

    @Override
    public void preExecute(final Request request) throws MaestroCheckedException {

        final HashSet<String> ids = getIdMappingsOfAddExecutor(request);

        if (!ids.isEmpty()) {
            final Collection<Operation> allOperations = getAllOperations(request);

            for (final Operation op : allOperations) {
                if (ids.contains(op.getId())) {
                    processLimitedToLibraryConfig(op, request.getConfig(), request.getContext().getUser());
                }
            }
        }
    }

    protected static void processLimitedToLibraryConfig(final Operation operation, final Config config, final User user) throws MaestroCheckedException {
        final boolean limitedToLibraryConfig = isUserLimitedToLibraryConfig((String) config.getProperty(AddExecutorHandler.CUSTOM_CONFIG_AUTHS), user.getOpAuths());
        final boolean hasConfig = nonNull(operation.get(AddExecutorHandler.EXECUTOR));
        if (limitedToLibraryConfig && hasConfig) {
            throw new MaestroCheckedException(String.format(USER_IS_LIMITED_TO_ONLY_USING_PARENT_PROPERTIES_ID_FROM_GRAPHLIBRARY_BUT_FOUND_STORE_PROPERTIES_S, ((Config) operation.get(AddExecutorHandler.EXECUTOR)).getId()));
        }
    }

    protected static boolean isUserLimitedToLibraryConfig(final String executorAuthsForCustomProperties, final Set<String> userOpAuths) {
        return nonNull(executorAuthsForCustomProperties)
                && Collections.disjoint(userOpAuths, Sets.newHashSet(executorAuthsForCustomProperties.split(",")));

    }

    protected static Collection<Operation> getAllOperations(final Request request) {
        final Operation operation = request.getOperation();
        final Collection<Operation> operations;
        if (operation instanceof Operations) {
            operations = ((Operations) operation).getOperations();
        } else {
            operations = new HashSet<>();
            operations.add(operation);
        }
        return operations;
    }

    protected static HashSet<String> getIdMappingsOfAddExecutor(final Request request) {
        final HashSet<String> ids = new HashSet<>();
        for (final Map.Entry<String, OperationHandler> entry : request.getConfig().getOperationHandlers().entrySet()) {
            if (entry.getValue() instanceof AddExecutorHandler) {
                ids.add(entry.getKey());
            }
        }
        return ids;
    }
}
