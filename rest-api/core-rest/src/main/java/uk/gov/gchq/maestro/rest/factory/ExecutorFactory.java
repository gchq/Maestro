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

package uk.gov.gchq.maestro.rest.factory;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.rest.SystemProperty;

/**
 * A {@code ExecutorFactory} creates instances of {@link uk.gov.gchq.maestro.Executor} to be reused for all queries.
 */
public interface ExecutorFactory {

    static ExecutorFactory createExecutorFactory() {
        final String executorFactoryClass = System.getProperty(SystemProperty.EXECUTOR_FACTORY_CLASS,
                SystemProperty.EXECUTOR_FACTORY_CLASS_DEFAULT);

        try {
            return Class.forName(executorFactoryClass)
                    .asSubclass(ExecutorFactory.class)
                    .newInstance();
        } catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to create executor factory from class: " + executorFactoryClass, e);
        }
    }

    /**
     * Create a new {@link Executor} instance.
     *
     * @return the Executor
     */
    Executor createExecutor();

    /**
     * Get the {@link Executor} instance.
     *
     * @return the graph
     */
    Executor getExecutor();
}
