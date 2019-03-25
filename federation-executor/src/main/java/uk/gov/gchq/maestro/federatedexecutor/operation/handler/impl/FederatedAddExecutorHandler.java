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

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.federatedexecutor.operation.AddExecutor;
import uk.gov.gchq.maestro.federatedexecutor.operation.handler.FederatedAddExecutorHandlerParent;
import uk.gov.gchq.maestro.util.Config;

/**
 * A handler for {@link AddExecutor} operation for the FederatedStore.
 *
 * @see FederatedAddExecutorHandlerParent
 */
public class FederatedAddExecutorHandler extends FederatedAddExecutorHandlerParent<AddExecutor> {

    @Override
    protected Executor _makeExecutor(final AddExecutor operation, final Executor executor) {
        return new Executor()
                .config(new Config().id(operation.getId())
                                .id(operation.getId())
                                .setProperties(operation.getConfig().getProperties().getProperties())
                        //.parentSchemaIds(operation.getParentSchemaIds()) TODO
                        //.parentStorePropertiesId(operation.getParentConfigId()) TODO
                );

    }


}
