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

package uk.gov.gchq.maestro.rest.application;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;

import uk.gov.gchq.maestro.rest.FactoriesBinder;
import uk.gov.gchq.maestro.rest.SystemProperty;
import uk.gov.gchq.maestro.rest.mapper.GafferCheckedExceptionMapper;
import uk.gov.gchq.maestro.rest.mapper.GafferRuntimeExceptionMapper;
import uk.gov.gchq.maestro.rest.mapper.GenericExceptionMapper;
import uk.gov.gchq.maestro.rest.mapper.ProcessingExceptionMapper;
import uk.gov.gchq.maestro.rest.mapper.UnauthorisedExceptionMapper;
import uk.gov.gchq.maestro.rest.mapper.WebApplicationExceptionMapper;
import uk.gov.gchq.maestro.rest.serialisation.RestJsonProvider;
import uk.gov.gchq.maestro.rest.serialisation.TextMessageBodyWriter;
import uk.gov.gchq.maestro.rest.service.v1.ExecutorConfigurationService;
import uk.gov.gchq.maestro.rest.service.v1.OperationService;
import uk.gov.gchq.maestro.rest.service.v1.StatusService;

import java.util.HashSet;
import java.util.Set;

/**
 * An {@code ApplicationConfig} sets up the application resources,
 * and any other application-specific configuration.
 */
public class ApplicationConfig extends ResourceConfig {
    protected final Set<Class<?>> resources = new HashSet<>();

    static final String VERSION = "v1";

    public ApplicationConfig() {
        addSystemResources();
        addExceptionMappers();
        addServices();
        setupBeanConfig();
        registerClasses(resources);
        register(new FactoriesBinder());
    }

    protected void addSystemResources() {
        resources.add(ApiListingResource.class);
        resources.add(SwaggerSerializers.class);
        resources.add(RestJsonProvider.class);
        resources.add(TextMessageBodyWriter.class);
    }

    protected void addExceptionMappers() {
        resources.add(UnauthorisedExceptionMapper.class);
        resources.add(GafferCheckedExceptionMapper.class);
        resources.add(GafferRuntimeExceptionMapper.class);
        resources.add(ProcessingExceptionMapper.class);
        resources.add(WebApplicationExceptionMapper.class);
        resources.add(GenericExceptionMapper.class);
    }

    protected void setupBeanConfig() {
        final BeanConfig beanConfig = new BeanConfig();

        String basePath = System.getProperty(SystemProperty.BASE_PATH, SystemProperty.BASE_PATH_DEFAULT);
        if (basePath.length() > 0 && !basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }

        beanConfig.setBasePath(basePath + '/' + VERSION);

        beanConfig.setConfigId(VERSION);
        beanConfig.setScannerId(VERSION);

        beanConfig.setResourcePackage("uk.gov.gchq.maestro.rest.service.v1");
        beanConfig.setScan(true);
    }

    protected void addServices() {
        resources.add(StatusService.class);
        resources.add(OperationService.class);
        resources.add(ExecutorConfigurationService.class);
    }

}
