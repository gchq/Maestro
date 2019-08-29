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

package uk.gov.gchq.maestro.federated.util;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.federated.FederatedAccess;
import uk.gov.gchq.maestro.federated.FederatedExecutorStorage;
import uk.gov.gchq.maestro.federated.operation.AddExecutor;
import uk.gov.gchq.maestro.federated.operation.RemoveExecutor;
import uk.gov.gchq.maestro.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public final class FederatedHandlersUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);
    public static final String ERROR_GETTING_S_FROM_S_S = "Error getting: %s from: %s -> %s";

    private FederatedHandlersUtil() {
        //No instance
    }

    public static List<Executor> getExecutorsFrom(final Executor executor, final User user, final String executorId) throws MaestroCheckedException {
        final ArrayList<String> executorIds = Lists.newArrayList(executorId);
        return getExecutorsFrom(executor, user, executorIds);
    }

    public static List<Executor> getExecutorsFrom(final Executor executor, final User user, final ArrayList<String> executorIds) throws MaestroCheckedException {
        requireNonNull(executor);
        final Config config = executor.getConfig();
        try {
            return getExecutorsFrom(config, user, executorIds);
        } catch (final MaestroCheckedException e) {
            throw new MaestroCheckedException(String.format(ERROR_GETTING_S_FROM_S_S, executorIds.toString(), config.getId(), e.getMessage()), e);
        }
    }

    public static List<Executor> getExecutorsFrom(final Config config, final User user, final List<String> executorIds) throws MaestroCheckedException {
        requireNonNull(config);
        final Properties properties = config.getProperties();
        return getExecutorsFrom(properties, user, executorIds);
    }

    public static List<Executor> getExecutorsFrom(final Properties properties, final User user, final List<String> executorIds) throws MaestroCheckedException {
        requireNonNull("Can't get Executors with null ids");
        List<Executor> rtn = new ArrayList<>();
        final FederatedExecutorStorage deserialisedExecutorStorage = FederatedPropertiesUtil.getDeserialisedExecutorStorage(properties);
        if (nonNull(deserialisedExecutorStorage)) {
            rtn.addAll(deserialisedExecutorStorage.get(user, executorIds));
        }
        return rtn;
    }

    public static Executor addExecutorTo(final AddExecutor op, final Executor parent, final String userId) throws MaestroCheckedException {
        requireNonNull(op);
        final FederatedAccess access = new FederatedAccess(op.getAuths(), userId, op.isPublic(), op.isDisabledByDefault());
        addExecutorTo(op.getExecutor(), parent.getConfig(), access);
        return parent;
    }

    public static Config addExecutorTo(final Executor addingExecutor, final Config config, final FederatedAccess access) throws MaestroCheckedException {
        addExecutorTo(addingExecutor, config.getProperties(), access);
        return config;
    }

    public static Properties addExecutorTo(final Executor addingExecutor, final Properties properties, final FederatedAccess federatedAccess) throws MaestroCheckedException {
        final FederatedExecutorStorage storage = new FederatedExecutorStorage()
                .put(addingExecutor, federatedAccess);

        FederatedPropertiesUtil.addSerialisedExecutorStorage(properties, storage);

        return properties;
    }

    public static boolean removeExecutorsFrom(final Executor executor, final RemoveExecutor op, final User user) throws MaestroCheckedException {
        requireNonNull(op);
        return removeExecutorsFrom(executor, op.getGraphId(), user);
    }

    public static boolean removeExecutorsFrom(final Executor executor, final String executorId, final User user) throws MaestroCheckedException {
        requireNonNull(executor);
        return removeExecutorsFrom(executor.getConfig(), executorId, user);
    }

    public static boolean removeExecutorsFrom(final Config config, final String executorId, final User user) throws MaestroCheckedException {
        requireNonNull(config);
        return removeExecutorsFrom(config.getProperties(), executorId, user);
    }

    public static boolean removeExecutorsFrom(final Properties properties, final String executorId, final User user) throws MaestroCheckedException {
        final FederatedExecutorStorage deserialisedExecutorStorage = FederatedPropertiesUtil.getDeserialisedExecutorStorage(properties);
        final boolean isRemoved = deserialisedExecutorStorage.remove(executorId, user);
        FederatedPropertiesUtil.putSerialisedExecutorStorage(properties, deserialisedExecutorStorage);
        return isRemoved;
    }

    public static Collection<String> getAllExecutorsFrom(final Executor executor, final User user) throws MaestroCheckedException {
        final Config config = executor.getConfig();
        return getAllExecutorsFrom(config, user);
    }

    public static Collection<String> getAllExecutorsFrom(final Config config, final User user) throws MaestroCheckedException {
        final Properties properties = config.getProperties();
        return getAllExecutorsFrom(properties, user);
    }

    public static Collection<String> getAllExecutorsFrom(final Properties properties, final User user) throws MaestroCheckedException {
        final FederatedExecutorStorage deserialisedExecutorStorage = FederatedPropertiesUtil.getDeserialisedExecutorStorage(properties);
        return deserialisedExecutorStorage.getAllIds(user);
    }
}
