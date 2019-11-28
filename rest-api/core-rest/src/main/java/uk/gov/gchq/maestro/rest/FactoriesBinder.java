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
package uk.gov.gchq.maestro.rest;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import uk.gov.gchq.maestro.rest.factory.ExecutorFactory;
import uk.gov.gchq.maestro.rest.factory.UserFactory;

import javax.inject.Singleton;

/**
 * HK2 binder class to facilitate dependency injection with Jersey.
 * Any dependency which has the {@link javax.inject.Inject} annotation can be
 * included. This denoted which concrete instance is bound to a particular
 * interface, and optionally in which scope the binding applies.
 */
public class FactoriesBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(getDefaultExecutorFactory()).to(ExecutorFactory.class).in(Singleton.class);
        bind(getDefaultUserFactory()).to(UserFactory.class).in(Singleton.class);
    }

    private Class<? extends ExecutorFactory> getDefaultExecutorFactory() {
        return getFactory(SystemProperty.EXECUTOR_FACTORY_CLASS, SystemProperty.EXECUTOR_FACTORY_CLASS_DEFAULT, ExecutorFactory.class);
    }

    private Class<? extends UserFactory> getDefaultUserFactory() {
        return getFactory(SystemProperty.USER_FACTORY_CLASS, SystemProperty.USER_FACTORY_CLASS_DEFAULT, UserFactory.class);
    }

    private <F> Class<? extends F> getFactory(final String factoryKey, final String defaultFactory, final Class<F> factoryClass) {
        try {
            return Class.forName(System.getProperty(factoryKey, defaultFactory))
                    .asSubclass(factoryClass);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to create user factory from class: " + factoryClass, e);
        }
    }
}
