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

import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;

import uk.gov.gchq.maestro.rest.FactoriesBinder;
import uk.gov.gchq.maestro.rest.mapper.MaestroCheckedExceptionMapper;
import uk.gov.gchq.maestro.rest.mapper.MaestroRuntimeExceptionMapper;
import uk.gov.gchq.maestro.rest.mapper.GenericExceptionMapper;
import uk.gov.gchq.maestro.rest.mapper.ProcessingExceptionMapper;
import uk.gov.gchq.maestro.rest.mapper.UnauthorisedExceptionMapper;
import uk.gov.gchq.maestro.rest.mapper.WebApplicationExceptionMapper;
import uk.gov.gchq.maestro.rest.serialisation.RestJsonProvider;
import uk.gov.gchq.maestro.rest.serialisation.TextMessageBodyWriter;

import java.util.HashSet;
import java.util.Set;

/**
 * An {@code ApplicationConfig} sets up the application resources,
 * and any other application-specific configuration.
 */
public abstract class ApplicationConfig extends ResourceConfig {
    protected final Set<Class<?>> resources = new HashSet<>();

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
        resources.add(MaestroCheckedExceptionMapper.class);
        resources.add(MaestroRuntimeExceptionMapper.class);
        resources.add(ProcessingExceptionMapper.class);
        resources.add(WebApplicationExceptionMapper.class);
        resources.add(GenericExceptionMapper.class);
    }

    /**
     * Should add version-specific classes to the collection of resources.
     */
    protected abstract void addServices();

    /**
     * Should set various properties for Swagger's initialization.
     */
    protected abstract void setupBeanConfig();

}
