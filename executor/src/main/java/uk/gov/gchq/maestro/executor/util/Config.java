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
package uk.gov.gchq.maestro.executor.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.koryphe.util.ReflectionUtil;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.ToStringBuilder;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.executor.hook.Hook;
import uk.gov.gchq.maestro.executor.hook.HookPath;
import uk.gov.gchq.maestro.executor.library.Library;
import uk.gov.gchq.maestro.executor.operation.declaration.OperationDeclaration;
import uk.gov.gchq.maestro.executor.operation.declaration.OperationDeclarations;
import uk.gov.gchq.maestro.executor.operation.handler.DefaultHandler;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.Operation;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

@JsonPropertyOrder(value = {"class", "id", "description", "operationHandlers", "hooks", "properties", "library"}, alphabetic = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
public class Config implements Comparable<Config>, Serializable {
    private static final long serialVersionUID = -2117037145523533002L;
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
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    private Map<String, Object> properties = new HashMap<>(); //TODO review in Config.compareTo()

    /**
     * The operation handlers - A Map containing all classes of operations
     * supported by this Executor, and an instance of all the OperationHandlers
     * that will be used to handle these operations.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
    private Map<String, OperationHandler> operationHandlers = new TreeMap<>(String::compareToIgnoreCase);

    private OperationHandler defaultHandler = new DefaultHandler();

    private Library library;

    public Config() {
    }

    public Config(final String id) {
        this.id = id;
    }

    @JsonCreator
    public Config(@JsonProperty("id") final String id, @JsonProperty("description") final String description, @JsonProperty("requestHooks") final List<Hook> requestHooks, @JsonProperty("operationHooks") final List<Hook> operationHooks, @JsonProperty("properties") final Map<String, Object> properties, @JsonProperty("operationHandlers") final Map<String, OperationHandler> operationHandlers, @JsonProperty("library") final Library library, @JsonProperty("defaultHandler") final OperationHandler defaultHandler) {
        this.id = id;
        this.description = description;
        setRequestHooks(requestHooks);
        setOperationHooks(operationHooks);
        this.properties = properties;
        setOperationHandlers(operationHandlers);
        this.library = library;
        setDefaultHandler(defaultHandler);
    }

    public static Config getConfigFromPath(final Class clazz, final String configResourcePath) throws uk.gov.gchq.maestro.commonutil.exception.SerialisationException {
        return JSONSerialiser.deserialise(StreamUtil.openStream(clazz, configResourcePath), Config.class);
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
        this.requestHooks.clear();
        if (nonNull(requestHooks)) {
            requestHooks.forEach(this::addRequestHook);
        }
    }

    public List<Hook> getOperationHooks() {
        return operationHooks;
    }

    public void setOperationHooks(final List<Hook> operationHooks) {
        this.operationHooks.clear();
        if (nonNull(operationHooks)) {
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

    public Config library(final Library library) {
        this.library = library;
        return this;
    }

    public Config addRequestHook(final Hook surroundingHook) {
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
        return this;
    }

    public Config addOperationHook(final Hook operationHook) {
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
        return this;
    }

    /**
     * Get this Executor's Properties.
     *
     * @return the instance of Properties,
     * this may contain details such as database connection details.
     */
    public Map<String, Object> getProperties() {
        return properties; //TODO ?
    }

    public Config setProperties(final Map<String, Object> properties) {
        if (nonNull(properties)) {
            if (isNull(this.properties)) {
                this.properties = new HashMap<>();
            }
            this.properties = ExecutorPropertiesUtil.loadProperties(properties);

            ReflectionUtil.addReflectionPackages(ExecutorPropertiesUtil.getReflectionPackages(this));
            updateJsonSerialiser();
        }
        return this;
    }

    public Config addProperties(final Map<String, Object> properties) {
        if (nonNull(properties)) {
            if (isNull(this.properties)) {
                this.properties = new HashMap<>();
            }

            this.properties.putAll(properties);

            ReflectionUtil.addReflectionPackages(ExecutorPropertiesUtil.getReflectionPackages(this));
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
                ExecutorPropertiesUtil.getOperationDeclarationPaths(this);
        if (null != declarationsPaths) {
            declarations = OperationDeclarations.fromPaths(declarationsPaths);
        }

        if (null == declarations) {
            declarations = new OperationDeclarations.Builder().build();
        }

        return declarations;
    }

    public static void updateJsonSerialiser(final Config config) {
        if (null != config) {
            JSONSerialiser.update(
                    ExecutorPropertiesUtil.getJsonSerialiserClass(config),
                    ExecutorPropertiesUtil.getJsonSerialiserModules(config),
                    ExecutorPropertiesUtil.getStrictJson(config)
            );
        } else {
            JSONSerialiser.update();
        }
    }

    public void updateJsonSerialiser() {
        updateJsonSerialiser(this);
    }

    public Config addOperationHandlers(final Map<String, OperationHandler> operationHandlers) {
        requireNonNull(operationHandlers);
        for (final Map.Entry<String, OperationHandler> entry : operationHandlers.entrySet()) {
            addOperationHandler(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Config setOperationHandlers(final Map<String, OperationHandler> operationHandlers) {
        this.operationHandlers.clear();
        if (nonNull(operationHandlers)) {
            addOperationHandlers(operationHandlers);
        }
        return this;
    }

    public Config addOperationHandler(final OperationDeclaration opDec) {
        return addOperationHandler(opDec.getOperationId(), opDec.getHandler());
    }

    public Config addOperationHandler(final String opId, final OperationHandler handler) {
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

        final Config that = (Config) o;

        final EqualsBuilder equalsBuilder = new EqualsBuilder();

        equalsBuilder.append(id, that.id)
                .append(description, that.description)
                .append(requestHooks, that.requestHooks)
                .append(operationHooks, that.operationHooks)
                .append(operationHandlers, that.operationHandlers)
                .append(defaultHandler, that.defaultHandler)
                .append(nonNull(properties), nonNull(that.properties));

        if (equalsBuilder.isEquals()
                && nonNull(properties)
                && equalsBuilder.append(properties.size(), that.properties.size()).isEquals()) {


            for (final String s : properties.keySet()) {
                if (!equalsBuilder.append(true, that.properties.containsKey(s)).isEquals()
                        || !equalsBuilder.append(properties.get(s), that.properties.get(s)).isEquals()) {
                    break;
                }
            }
        }

        return equalsBuilder.isEquals(); //TODO simplify this method
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
                .append(library) //TODO review equals library
                .append(defaultHandler)
                .toHashCode();
    }

    public byte[] serialise() throws SerialisationException {
        return JSONSerialiser.serialise(this, true);
    }

    public Object getPropertyOrDefault(final Object key, final Object defaultValue) {
        return (String) getProperties().getOrDefault(key, defaultValue);
    }

    public Object getProperty(final Object key) {
        return getPropertyOrDefault(key, null);
    }

    public String setProperty(final String key, final Object value) {
        return (String) properties.put(key, value);
    }

    public OperationHandler getDefaultHandler() {
        return defaultHandler;
    }

    public Config setDefaultHandler(final OperationHandler defaultHandler) {
        if (nonNull(defaultHandler)) {
            this.defaultHandler = defaultHandler;
        }
        return this;
    }

    @Override
    public int compareTo(final Config that) {
        requireNonNull(that, "tried to compare null object");
        // final CompareToBuilder cb = new CompareToBuilder();
        // cb.append(this.id, that.id)
        //         .append(this.description, that.description)
        //         .append(this.requestHooks, that.requestHooks)
        //         .append(this.operationHooks, that.operationHooks, (Comparator<Hook>) (o1, o2) -> {
        //             try {
        //                 return new String(JSONSerialiser.serialise(o1))
        //                         .compareTo(new String(JSONSerialiser.serialise(o2)));
        //             } catch (SerialisationException e) {
        //                 throw new MaestroRuntimeException("Error comparing hooks", e);
        //             }
        //         })
        //         .append(this.operationHandlers, that.operationHandlers)
        //         .append(this.defaultHandler, that.defaultHandler)
        //         .append(nonNull(this.properties), nonNull(that.properties))
        //         .append(this.properties, that.properties);
        // return cb.toComparison();

        return this.toString().compareTo(that.toString()); //TODO improve

    }


}
