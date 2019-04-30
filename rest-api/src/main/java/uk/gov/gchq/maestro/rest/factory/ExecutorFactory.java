/*
 * Copyright 2016-2019 Crown Copyright
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
import uk.gov.gchq.maestro.util.Config;

import java.nio.file.Paths;

/**
 * A {@code GraphFactory} creates instances of {@link Executor} to be reused for all queries.
 */
public class ExecutorFactory {
    private Executor executor;

    public ExecutorFactory() {
        // public constructor is required only by HK2
    }

    /**
     * Create a new {@link Executor} instance.
     * <p></p>
     *
     * @return the Executor
     */
    public Executor createExecutor() {
        final Config.Builder configBuilder = new Config.Builder();

        final String maestroConfigPath = System.getProperty(SystemProperty.MAESTRO_CONFIG_PATH);
        if (null != maestroConfigPath) {
            configBuilder.merge(Paths.get(maestroConfigPath));
        }

        return new Executor().config(configBuilder.build());
    }

    /**
     * Get the {@link Executor} instance.
     *
     * @return the executor
     */
    public Executor getExecutor() {
        return executor;
    }
}

