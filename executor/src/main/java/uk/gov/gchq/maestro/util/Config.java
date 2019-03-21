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
package uk.gov.gchq.maestro.util;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.maestro.StoreProperties;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.ToStringBuilder;
import uk.gov.gchq.maestro.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.library.Library;
import uk.gov.gchq.maestro.library.NoLibrary;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclaration;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclarations;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.util.hook.Hook;
import uk.gov.gchq.maestro.util.hook.HookPath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static uk.gov.gchq.koryphe.util.ReflectionUtil.addReflectionPackages;
import static uk.gov.gchq.maestro.operation.declaration.OperationDeclarations.fromJson;

@JsonPropertyOrder(value = {"class", "id", "description", "operationHandlers", "hooks", "properties", "library"}, alphabetic = true)
public class Config {
    /**
     * The id of the store.
     */
    private String id;

    /**
     * A short description of the store
     */
    private String description;

    /**
     * A list of {@link Hook}s
     */
    private List<Hook> hooks = new ArrayList<>();

    /**
     * The store properties - contains specific configuration information for
     * the store - such as database connection strings.
     */
    private StoreProperties properties = new StoreProperties();

    /**
     * The operation handlers - A Map containing all classes of operations
     * supported by this store, and an instance of all the OperationHandlers
     * that will be used to handle these operations.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    private final Map<Class<? extends Operation>, OperationHandler> operationHandlers = new LinkedHashMap<>();

    private Library library;

    public Config() {
    }

    public Config(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public List<Hook> getHooks() {
        return hooks;
    }

    public void setHooks(final List<Hook> hooks) {
        if (null == hooks) {
            this.hooks.clear();
        } else {
            hooks.forEach(this::addHook);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    public Library getLibrary() {
        return library;
    }

    public void setLibrary(final Library library) {
        this.library = library;
    }

    public void addHook(final Hook hook) {
        if (null != hook) {
            if (hook instanceof HookPath) {
                final String path = ((HookPath) hook).getPath();
                final File file = new File(path);
                if (!file.exists()) {
                    throw new IllegalArgumentException("Unable to find graph hook file: " + path);
                }
                try {
                    hooks.add(JSONSerialiser.deserialise(FileUtils.readFileToByteArray(file), Hook.class));
                } catch (final IOException e) {
                    throw new IllegalArgumentException("Unable to deserialise graph hook from file: " + path, e);
                }
            } else {
                hooks.add(hook);
            }
        }
    }

    /**
     * Get this Store's {@link StoreProperties}.
     *
     * @return the instance of {@link StoreProperties},
     * this may contain details such as database connection details.
     */
    public StoreProperties getProperties() {
        return properties;
    }

    @JsonGetter("properties")
    public Properties _getProperties() {
        return isNull(properties) ? null : properties.getProperties();
    }

    @JsonSetter
    public void setProperties(final Properties properties) {
        if (nonNull(properties)) {
            if (isNull(this.properties)) {
                this.properties = new StoreProperties();
            }
            this.properties.setProperties(properties);
        }
    }

    /**
     * Returns the operation definitions from the file specified in the
     * properties.
     * This is an optional feature, so if the property does not exist then this
     * function
     * will return an empty object.
     *
     * @return The Operation Definitions to load dynamically
     */
    @JsonIgnore
    public OperationDeclarations getOperationDeclarations() {
        OperationDeclarations declarations = null;

        final String declarationsPaths =
                properties.get(StoreProperties.OPERATION_DECLARATIONS);
        if (null != declarationsPaths) {
            declarations = OperationDeclarations.fromPaths(declarationsPaths);
        }

        if (null == declarations) {
            declarations = new OperationDeclarations.Builder().build();
        }

        return declarations;
    }

    public void setProperties(final StoreProperties properties) {
        final Class<? extends StoreProperties> requiredPropsClass = getPropertiesClass();
        properties.updateStorePropertiesClass(requiredPropsClass);

        // If the properties instance is not already an instance of the required class then reload the properties
        if (requiredPropsClass.isAssignableFrom(properties.getClass())) {
            this.properties = properties;
        } else {
            this.properties = StoreProperties.loadStoreProperties(properties.getProperties());
        }

        addReflectionPackages(properties.getReflectionPackages());
        updateJsonSerialiser();
    }

    protected Class<? extends StoreProperties> getPropertiesClass() {
        return StoreProperties.class;
    }

    public static void updateJsonSerialiser(final StoreProperties storeProperties) {
        if (null != storeProperties) {
            JSONSerialiser.update(
                    storeProperties.getJsonSerialiserClass(),
                    storeProperties.getJsonSerialiserModules(),
                    storeProperties.getStrictJson()
            );
        } else {
            JSONSerialiser.update();
        }
    }

    public void updateJsonSerialiser() {
        updateJsonSerialiser(properties);
    }

    public <OP extends Operation> void addOperationHandler(final Class<? extends Operation> opClass, final OperationHandler<OP> handler) {
        if (null == handler) {
            operationHandlers.remove(opClass);
        } else {
            operationHandlers.put(opClass, handler);
        }
    }

    public OperationHandler<? extends Operation> getOperationHandler(final Class<?
            extends Operation> opClass) {
        return operationHandlers.get(opClass);
    }

    public Map<Class<? extends Operation>, OperationHandler> getOperationHandlers() {
        return operationHandlers;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("description", description)
                .append("hooks", hooks)
                .append("properties", properties)
                .append("operationHandlers", operationHandlers)
                .toString();
    }

    public static class Builder {
        private Config config = new Config();
        private List<Hook> hooks;
        private StoreProperties properties;
        Map<Class<? extends Operation>, OperationHandler> operationHandlers;

        // Config
        public Builder config(final Config config) {
            this.config = config;
            return this;
        }

        // Id
        public Builder id(final String id) {
            config.setId(id);
            return this;
        }

        // Description
        public Builder description(final String description) {
            config.setDescription(description);
            return this;
        }

        public Builder library(final Library library) {
            this.config.setLibrary(library);
            return this;
        }

        // StoreProperties
        public Builder storeProperties(final Properties properties) {
            return storeProperties(null != properties ? StoreProperties.loadStoreProperties(properties) : null);
        }

        public Builder storeProperties(final StoreProperties properties) {
            this.properties = properties;
            if (null != properties) {
                addReflectionPackages(properties.getReflectionPackages());
                JSONSerialiser.update(
                        properties.getJsonSerialiserClass(),
                        properties.getJsonSerialiserModules(),
                        properties.getStrictJson()
                );
            }
            return this;
        }

        public Builder storeProperties(final String propertiesPath) {
            return storeProperties(null != propertiesPath ? StoreProperties.loadStoreProperties(propertiesPath) : null);
        }

        public Builder storeProperties(final Path propertiesPath) {
            if (null == propertiesPath) {
                properties = null;
            } else {
                storeProperties(StoreProperties.loadStoreProperties(propertiesPath));
            }
            return this;
        }

        public Builder storeProperties(final InputStream propertiesStream) {
            if (null == propertiesStream) {
                properties = null;
            } else {
                storeProperties(StoreProperties.loadStoreProperties(propertiesStream));
            }
            return this;
        }

        public Builder storeProperties(final URI propertiesURI) {
            if (null != propertiesURI) {
                try {
                    storeProperties(StreamUtil.openStream(propertiesURI));
                } catch (final IOException e) {
                    throw new IllegalArgumentException("Unable to read storeProperties from URI: " + propertiesURI, e);
                }
            }

            return this;
        }

        public Builder addStoreProperties(final Properties properties) {
            if (null != properties) {
                addStoreProperties(StoreProperties.loadStoreProperties(properties));
            }
            return this;
        }

        public Builder addStoreProperties(final StoreProperties updateProperties) {
            if (null != updateProperties) {
                if (null == this.properties) {
                    storeProperties(updateProperties);
                } else {
                    this.properties.merge(updateProperties);
                }
            }
            return this;
        }

        public Builder addStoreProperties(final String updatePropertiesPath) {
            if (null != updatePropertiesPath) {
                addStoreProperties(StoreProperties.loadStoreProperties(updatePropertiesPath));
            }
            return this;
        }

        public Builder addStoreProperties(final Path updatePropertiesPath) {
            if (null != updatePropertiesPath) {
                addStoreProperties(StoreProperties.loadStoreProperties(updatePropertiesPath));
            }
            return this;
        }

        public Builder addStoreProperties(final InputStream updatePropertiesStream) {
            if (null != updatePropertiesStream) {
                addStoreProperties(StoreProperties.loadStoreProperties(updatePropertiesStream));
            }
            return this;
        }

        public Builder addStoreProperties(final URI updatePropertiesURI) {
            if (null != updatePropertiesURI) {
                try {
                    addStoreProperties(StreamUtil.openStream(updatePropertiesURI));
                } catch (final IOException e) {
                    throw new IllegalArgumentException("Unable to read storeProperties from URI: " + updatePropertiesURI, e);
                }
            }
            return this;
        }

        // Json config builder
        public Builder json(final Path path) {
            try {
                return json(null != path ? Files.readAllBytes(path) : null);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Unable to read config from path: " + path, e);
            }
        }

        public Builder json(final URI uri) {
            try {
                json(null != uri ? StreamUtil.openStream(uri) : null);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Unable to read config from uri: " + uri, e);
            }

            return this;
        }

        public Builder json(final InputStream stream) {
            try {
                json(null != stream ? IOUtils.toByteArray(stream) : null);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Unable to read config from input stream", e);
            }

            return this;
        }

        public Builder json(final byte[] bytes) {
            if (null != bytes) {
                try {
                    merge(JSONSerialiser.deserialise(bytes, Config.class));
                } catch (final IOException e) {
                    throw new IllegalArgumentException("Unable to deserialise config", e);
                }
            }
            return this;
        }

        // Merge configs
        public Builder merge(final Config config) {
            if (null != config) {
                if (null != this.config.getId()) {
                    this.config.setId(config.getId());
                }
                if (null != this.config.getDescription()) {
                    this.config.setDescription(config.getDescription());
                }
                config.getHooks().forEach(hook -> this.config.addHook(hook));
                this.config.getProperties().merge(config.getProperties());
                this.config.getOperationHandlers().putAll(config.getOperationHandlers());
            }
            return this;
        }

        public Builder merge(final String uri) {
            if (null != uri) {
                merge(Paths.get(uri));
            }
            return this;
        }

        public Builder merge(final Path path) {
            if (null != path) {
                try {
                    merge(JSONSerialiser.deserialise(null != path ?
                                    Files.readAllBytes(path) : null,
                            config.getClass()));
                } catch (final IOException e) {
                    throw new IllegalArgumentException("Unable to read graph " +
                            "config from path: " + path, e);
                }
            }
            return this;
        }

        public Builder merge(final InputStream stream) {
            try {
                merge(JSONSerialiser.deserialise(null != stream ?
                                IOUtils.toByteArray(stream) : null,
                        config.getClass()));
            } catch (
                    final IOException e) {
                throw new IllegalArgumentException("Unable to read graph config from input stream", e);
            }
            return this;
        }

        // Hooks
        public Builder addHooks(final Path hooksPath) {
            if (null == hooksPath || !hooksPath.toFile().exists()) {
                throw new IllegalArgumentException("Unable to find graph hooks file: " + hooksPath);
            }
            final Hook[] hooks;
            try {
                hooks =
                        JSONSerialiser.deserialise(FileUtils.readFileToByteArray(hooksPath.toFile()), Hook[].class);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Unable to load graph hooks file: " + hooksPath, e);
            }
            return addHooks(hooks);
        }

        public Builder addHook(final Path hookPath) {
            if (null == hookPath || !hookPath.toFile().exists()) {
                throw new IllegalArgumentException("Unable to find graph hook file: " + hookPath);
            }

            final Hook hook;
            try {
                hook =
                        JSONSerialiser.deserialise(FileUtils.readFileToByteArray(hookPath.toFile()), Hook.class);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Unable to load graph hook file: " + hookPath, e);
            }
            return addHook(hook);
        }

        public Builder addHook(final Hook hook) {
            if (null != hook) {
                this.hooks.add(hook);
            }
            return this;
        }

        public Builder addHooks(final Hook... hooks) {
            if (null != hooks) {
                this.hooks.addAll(Arrays.asList(hooks));
            }
            return this;
        }

        public Builder operationHandlers(final String paths) {
            final OperationDeclarations allDefinitions = new OperationDeclarations.Builder().build();

            try {
                for (final String pathStr : paths.split(",")) {
                    final OperationDeclarations definitions;
                    final Path path = Paths.get(pathStr);
                    if (path.toFile().exists()) {
                        definitions = fromJson(Files.readAllBytes(path));
                    } else {
                        definitions = fromJson(StreamUtil.openStream(OperationDeclarations.class, pathStr));
                    }
                    if (null != definitions && null != definitions.getOperations()) {
                        allDefinitions.getOperations().addAll(definitions.getOperations());
                    }
                }
            } catch (final IOException e) {
                throw new RuntimeException("Failed to load Operation handlers" +
                        " from paths: " + paths + ". Due to " + e.getMessage(), e);
            }

            return operationHandlers(allDefinitions);
        }

        public Builder operationHandlers(final OperationDeclarations operationDeclarations) {
            operationDeclarations.getOperations().forEach(opDec -> operationHandler(opDec));
            return this;
        }

        public Builder operationHandler(final OperationDeclaration operationDeclaration) {
            if (null == operationHandlers) {
                operationHandlers = new LinkedHashMap<>();
            }
            operationHandlers.put(operationDeclaration.getOperation(),
                    operationDeclaration.getHandler());
            return this;
        }

        public Config build() {
            if (null == config.getLibrary()) {
                config.setLibrary(new NoLibrary());
            }
            config.getProperties().getProperties().putAll(properties.getProperties());
            config.getOperationHandlers().putAll(operationHandlers);
            return config;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Config config = (Config) o;

        final EqualsBuilder equalsBuilder = new EqualsBuilder()
                .append(id, config.id)
                .append(description, config.description)
                .append(hooks, config.hooks)
                .append(library, config.library)
                .append(operationHandlers, config.operationHandlers)
                .append(nonNull(properties), nonNull(config.properties));

        if (equalsBuilder.isEquals() && nonNull(properties)) {
            equalsBuilder.append(properties.getProperties(), config.properties.getProperties());
        }

        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(description)
                .append(hooks)
                .append(properties)
                .append(operationHandlers)
                .append(library)
                .toHashCode();
    }
}
