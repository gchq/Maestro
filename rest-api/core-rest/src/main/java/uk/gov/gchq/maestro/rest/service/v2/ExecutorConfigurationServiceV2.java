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

package uk.gov.gchq.maestro.rest.service.v2;

import uk.gov.gchq.koryphe.util.ReflectionUtil;
import uk.gov.gchq.maestro.rest.SystemProperty;
import uk.gov.gchq.maestro.rest.factory.ExecutorFactory;
import uk.gov.gchq.maestro.rest.factory.UserFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE;
import static uk.gov.gchq.maestro.rest.ServiceConstants.MAESTRO_MEDIA_TYPE_HEADER;

/**
 * An implementation of {@link IExecutorConfigurationServiceV2}. By default it will use a singleton
 * {@link uk.gov.gchq.maestro.Executor} generated using the {@link ExecutorFactory}.
 */
public class ExecutorConfigurationServiceV2 implements IExecutorConfigurationServiceV2 {
    @Inject
    private ExecutorFactory executorFactory;

    @Inject
    private UserFactory userFactory;

    public ExecutorConfigurationServiceV2() {
        updateReflectionPaths();
    }

    public static void initialise() {
        // Invoking this method will cause the static lists to be populated.
        updateReflectionPaths();
    }

    @Override
    public Response getDescription() {
        return Response.ok(executorFactory.getExecutor().getConfig().getDescription())
                .header(MAESTRO_MEDIA_TYPE_HEADER, MAESTRO_MEDIA_TYPE)
                .build();
    }

    private static void updateReflectionPaths() {
        ReflectionUtil.addReflectionPackages(System.getProperty(SystemProperty.PACKAGE_PREFIXES, SystemProperty.PACKAGE_PREFIXES_DEFAULT));
    }
}
