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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.koryphe.util.ReflectionUtil;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.ToStringBuilder;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.hook.Hook;
import uk.gov.gchq.maestro.hook.HookPath;
import uk.gov.gchq.maestro.library.Library;
import uk.gov.gchq.maestro.library.NoLibrary;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclaration;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclarations;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;

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
import java.util.TreeMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static uk.gov.gchq.maestro.operation.declaration.OperationDeclarations.fromJson;

@JsonPropertyOrder(value = {"class", "id", "description", "operationHandlers", "hooks", "properties", "library"}, alphabetic = true)
public class Config {
    /**
     * The id of the Executor.
     */
    private String id;

    /**
     * A short description of the Executor
     */
    private String description;

    /**
     * A list of {@link Hook}s that are run around the full Request that is
     * received.
     */
    private List<Hook> requestHooks = new ArrayList<>();

    /**
     * A list of {@link Hook}s that are run for each Operation within the
     * Request that is received.
     */
    private List<Hook> operationHooks = new ArrayList<>();

    /**
     * The Executor properties - contains specific configuration information for
     * the Executor - such as database connection strings.
     */
    private Properties properties = new Properties();

    /**
     * The operation handlers - A Map containing all classes of operations
     * supported by this Executor, and an instance of all the OperationHandlers
     * that will be used to handle these operations.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    private final Map<String, OperationHandler> operationHandlers = new TreeMap<>(String::compareToIgnoreCase);

    private Library library;

    public Config() {
    }

    public Config(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Config id(final String id) {
        this.id = id;
        return this;
    }

    public List<Hook> getRequestHooks() {
        return requestHooks;
    }

    public void setRequestHooks(final List<Hook> requestHooks) {
        if (null == requestHooks) {
            this.requestHooks.clear();
        } else {
            requestHooks.forEach(this::addRequestHook);
        }
    }

    public List<Hook> getOperationHooks() {
        return operationHooks;
    }

    public void setOperationHooks(final List<Hook> operationHooks) {
        if (null == operationHooks) {
            this.operationHooks.clear();
        } else {
            operationHooks.forEach(this::addOperationHook);
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

    public void addRequestHook(final Hook surroundingHook) {
        if (null != surroundingHook) {
            if (surroundingHook instanceof HookPath) {
                final String path = ((HookPath) surroundingHook).getPath();
                final File file = new File(path);
                if (!file.exists()) {
                    throw new IllegalArgumentException("Unable to find " +
                            "request hook file: " + path);
                }
                try {
                    requestHooks.add(JSONSerialiser.deserialise(FileUtils.readFileToByteArray(file), Hook.class));
                } catch (final IOException e) {
                    throw new IllegalArgumentException("Unable to deserialise" +
                            " request hook from file: " + path, e);
                }
            } else {
                requestHooks.add(surroundingHook);
            }
        }
    }

    public void addOperationHook(final Hook operationHook) {
        if (null != operationHook) {
            if (operationHook instanceof HookPath) {
                final String path = ((HookPath) operationHook).getPath();
                final File file = new File(path);
                if (!file.exists()) {
                    throw new IllegalArgumentException("Unable to find hook file: " + path);
                }
                try {
                    operationHooks.add(JSONSerialiser.deserialise(FileUtils.readFileToByteArray(file), Hook.class));
                } catch (final IOException e) {
                    throw new IllegalArgumentException("Unable to deserialise hook from file: " + path, e);
                }
            } else {
                operationHooks.add(operationHook);
            }
        }
    }

    /**
     * Get this Executor's {@link Properties}.
     *
     * @return the instance of {@link Properties},
     * this may contain details such as database connection details.
     */
    public Properties getProperties() {
        return isNull(properties) ? null : properties;
    }

    public Config setProperties(final Properties properties) {
        if (nonNull(properties)) {
            if (isNull(this.properties)) {
                this.properties = new Properties();
            }
            this.properties = ExecutorPropertiesUtil.loadProperties(properties);

            ReflectionUtil.addReflectionPackages(ExecutorPropertiesUtil.getReflectionPackages(properties));
            updateJsonSerialiser();
        }
        return this;
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
                ExecutorPropertiesUtil.getOperationDeclarationPaths(properties);
        if (null != declarationsPaths) {
            declarations = OperationDeclarations.fromPaths(declarationsPaths);
        }

        if (null == declarations) {
            declarations = new OperationDeclarations.Builder().build();
        }

        return declarations;
    }

    public static void updateJsonSerialiser(final Properties properties) {
        if (null != properties) {
            JSONSerialiser.update(
                    ExecutorPropertiesUtil.getJsonSerialiserClass(properties),
                    ExecutorPropertiesUtil.getJsonSerialiserModules(properties),
                    ExecutorPropertiesUtil.getStrictJson(properties)
            );
        } else {
            JSONSerialiser.update();
        }
    }

    public void updateJsonSerialiser() {
        updateJsonSerialiser(properties);
    }

    public <OP extends Operation> Config addOperationHandler(final String opId, final OperationHandler handler) {
        if (null == handler) {
            operationHandlers.remove(opId);
        } else {
            operationHandlers.put(opId, handler);
        }
        return this;
    }

    public OperationHandler getOperationHandler(final Operation op) {
        return operationHandlers.get(op.getId());
    }

    public boolean contains(final Operation op) {
        return contains(op.getId());
    }

    public boolean contains(final String id) {
        return operationHandlers.containsKey(id);
    }

    public Map<String, OperationHandler> getOperationHandlers() {
        return operationHandlers;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("description", description)
                .append("requestHooks", requestHooks)
                .append("operationHooks", operationHooks)
                .append("properties", properties)
                .append("operationHandlers", operationHandlers)
                .toString();
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
                .append(requestHooks, config.requestHooks)
                .append(operationHooks, config.operationHooks)
                .append(operationHandlers, config.operationHandlers)
                .append(nonNull(properties), nonNull(config.properties));

        if (equalsBuilder.isEquals() && nonNull(properties)) {
            equalsBuilder.append(properties, config.properties);
        }

        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(description)
                .append(requestHooks)
                .append(operationHooks)
                .append(properties)
                .append(operationHandlers)
                .append(library)
                .toHashCode();
    }

    public static class Builder {
        private Config config = new Config();
        private List<Hook> requestHooks = new ArrayList<>();
        private List<Hook> operationHooks = new ArrayList<>();
        private Properties properties = new Properties();
        Map<String, OperationHandler> operationHandlers = new LinkedHashMap<>();

        // Config
        public Builder config(final Config config) {
            this.config = config;
            return this;
        }

        // Id
        public Builder id(final String id) {
            config.id(id);
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

        // Properties

        public Builder executorProperties(final Properties properties) {
            if (null != this.properties) {
                this.properties.putAll(properties);
            } else {
                this.properties = properties;
            }
            if (null != properties) {
                ReflectionUtil.addReflectionPackages(ExecutorPropertiesUtil.getReflectionPackages(properties));
                JSONSerialiser.update(
                        ExecutorPropertiesUtil.getJsonSerialiserClass(properties),
                        ExecutorPropertiesUtil.getJsonSerialiserModules(properties),
                        ExecutorPropertiesUtil.getStrictJson(properties)
                );
            }
            return this;
        }

        public Builder executorProperties(final String propertiesPath) {
            return executorProperties(null != propertiesPath ?
                    ExecutorPropertiesUtil.loadProperties(propertiesPath) : null);
        }

        public Builder executorProperties(final Path propertiesPath) {
            if (null == propertiesPath) {
                properties = null;
            } else {
                executorProperties(ExecutorPropertiesUtil.loadProperties(propertiesPath));
            }
            return this;
        }

        public Builder executorProperties(final InputStream propertiesStream) {
            if (null == propertiesStream) {
                properties = null;
            } else {
                executorProperties(ExecutorPropertiesUtil.loadProperties(propertiesStream));
            }
            return this;
        }

        public Builder executorProperties(final URI propertiesURI) {
            if (null != propertiesURI) {
                try {
                    executorProperties(StreamUtil.openStream(propertiesURI));
                } catch (final IOException e) {
                    throw new IllegalArgumentException("Unable to read " +
                            "executorProperties from URI: " + propertiesURI, e);
                }
            }

            return this;
        }

        public Builder addProperties(final Properties updateProperties) {
            if (null != updateProperties) {
                if (null == this.properties) {
                    executorProperties(updateProperties);
                } else {
                    ExecutorPropertiesUtil.merge(this.properties, updateProperties);
                }
            }
            return this;
        }

        public Builder addProperties(final String updatePropertiesPath) {
            if (null != updatePropertiesPath) {
                addProperties(ExecutorPropertiesUtil.loadProperties(updatePropertiesPath));
            }
            return this;
        }

        public Builder addProperties(final Path updatePropertiesPath) {
            if (null != updatePropertiesPath) {
                addProperties(ExecutorPropertiesUtil.loadProperties(updatePropertiesPath));
            }
            return this;
        }

        public Builder addProperties(final InputStream updatePropertiesStream) {
            if (null != updatePropertiesStream) {
                addProperties(ExecutorPropertiesUtil.loadProperties(updatePropertiesStream));
            }
            return this;
        }

        public Builder addProperties(final URI updatePropertiesURI) {
            if (null != updatePropertiesURI) {
                try {
                    addProperties(StreamUtil.openStream(updatePropertiesURI));
                } catch (final IOException e) {
                    throw new IllegalArgumentException("Unable to read " +
                            "executorProperties from URI: " + updatePropertiesURI, e);
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
        public Builder merge(final Config thatConfig) {
            if (null != thatConfig) {
                if (null == this.config.getId()) {
                    this.config.id(thatConfig.getId());
                }
                if (null == this.config.getDescription()) {
                    this.config.setDescription(thatConfig.getDescription());
                }
                thatConfig.getRequestHooks().forEach(hook -> this.config.addRequestHook(hook));
                thatConfig.getOperationHooks().forEach(hook -> this.config.addOperationHook(hook));
                ExecutorPropertiesUtil.merge(this.config.getProperties(), thatConfig.getProperties());
                this.config.getOperationHandlers().putAll(thatConfig.getOperationHandlers());
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
                    throw new IllegalArgumentException("Unable to read " +
                            "Executor config from path: " + path, e);
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
                throw new IllegalArgumentException("Unable to read Executor " +
                        "config from input stream", e);
            }
            return this;
        }

        // Hooks
        public Builder addRequestHooks(final Path requestHooksPath) {
            if (null == requestHooksPath || !requestHooksPath.toFile().exists()) {
                throw new IllegalArgumentException("Unable to find request hooks file: " + requestHooksPath);
            }
            final Hook[] requestHooks;
            try {
                requestHooks =
                        JSONSerialiser.deserialise(FileUtils.readFileToByteArray(requestHooksPath.toFile()), Hook[].class);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Unable to load request hooks file: " + requestHooksPath, e);
            }
            return addRequestHooks(requestHooks);
        }

        public Builder addOperationHooks(final Path operationHooksPath) {
            if (null == operationHooksPath || !operationHooksPath.toFile().exists()) {
                throw new IllegalArgumentException("Unable to find operation " +
                        "hooks file: " + operationHooksPath);
            }
            final Hook[] operationHooks;
            try {
                operationHooks =
                        JSONSerialiser.deserialise(FileUtils.readFileToByteArray(operationHooksPath.toFile()), Hook[].class);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Unable to load operation " +
                        "hooks file: " + operationHooksPath, e);
            }
            return addOperationHooks(operationHooks);
        }

        public Builder addRequestHook(final Path requestHookPath) {
            if (null == requestHookPath || !requestHookPath.toFile().exists()) {
                throw new IllegalArgumentException("Unable to find request " +
                        "hook file: " + requestHookPath);
            }

            final Hook requestHook;
            try {
                requestHook =
                        JSONSerialiser.deserialise(FileUtils.readFileToByteArray(requestHookPath.toFile()), Hook.class);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Unable to load request " +
                        "hook file: " + requestHookPath, e);
            }
            return addRequestHook(requestHook);
        }

        public Builder addOperationHook(final Path operationHookPath) {
            if (null == operationHookPath || !operationHookPath.toFile().exists()) {
                throw new IllegalArgumentException("Unable to find operation " +
                        "hook file: " + operationHookPath);
            }

            final Hook operationHook;
            try {
                operationHook =
                        JSONSerialiser.deserialise(FileUtils.readFileToByteArray(operationHookPath.toFile()), Hook.class);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Unable to load operation " +
                        "hook file: " + operationHookPath, e);
            }
            return addOperationHook(operationHook);
        }

        public Builder addRequestHook(final Hook requestHook) {
            if (null != requestHook) {
                this.requestHooks.add(requestHook);
            }
            return this;
        }

        public Builder addOperationHook(final Hook operationHook) {
            if (null != operationHook) {
                this.operationHooks.add(operationHook);
            }
            return this;
        }

        public Builder addRequestHooks(final Hook... requestHooks) {
            if (null != requestHooks) {
                this.requestHooks.addAll(Arrays.asList(requestHooks));
            }
            return this;
        }

        public Builder addOperationHooks(final Hook... operationHooks) {
            if (null != operationHooks) {
                this.operationHooks.addAll(Arrays.asList(operationHooks));
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
            operationHandlers.put(operationDeclaration.getOperationId(),
                    operationDeclaration.getHandler());
            return this;
        }

        public Config build() {
            if (null == config.getLibrary()) {
                config.setLibrary(new NoLibrary());
            }
            config.setRequestHooks(requestHooks);
            config.setOperationHooks(operationHooks);
            config.getProperties().putAll(properties);
            config.getOperationHandlers().putAll(operationHandlers);
            return config;
        }
    }
}
