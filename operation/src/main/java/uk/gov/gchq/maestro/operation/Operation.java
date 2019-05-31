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
package uk.gov.gchq.maestro.operation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.koryphe.serialisation.json.JsonSimpleClassName;
import uk.gov.gchq.maestro.commonutil.Required;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import static java.util.Objects.isNull;

/**
 * An {@code Operation} defines an operation to be processed on an Executor.
 * All operations must to implement this interface.
 * Operations should be written to be as generic as possible to allow them to
 * be applied to different Executors.
 * NOTE - operations should not contain the operation logic. The logic should be separated out into a operation handler.
 * This will allow you to execute the same operation on different Executors
 * with different handlers.
 * <p>
 * Operations must be JSON serialisable in order to make REST API calls.
 * </p>
 * <p>
 * Any fields that are required should be annotated with the {@link Required} annotation.
 * </p>
 * <p>
 * Operation implementations need to implement this Operation interface and any of the following interfaces they wish to make use of:
 * uk.gov.gchq.maestro.operation.io.Input
 * uk.gov.gchq.maestro.operation.io.Output
 * uk.gov.gchq.maestro.operation.io.InputOutput (Use this instead of Input and Output if your operation takes both input and output.)
 * uk.gov.gchq.maestro.operation.io.MultiInput (Use this in addition if you operation takes multiple inputs. This will help with json  serialisation)
 * </p>
 * <p>
 * Each Operation impl should have a corresponding unit test class
 * that extends the BOperationTest class.
 * </p>
 * <p>
 * Implementations should override the close method and ensure all closeable fields are closed.
 * </p>
 * <p>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
@JsonPropertyOrder(value = {"class", "id", "operationArgs"}, alphabetic = true)
@JsonSimpleClassName(includeSubtypes = true)
@Since("0.0.1")
@Summary("An Operation which contains an Id and a mapping of args to be used by handlers associated by the Id.")
public class Operation {
    private final String id; //TODO requirement to be mutable?
    public static final Locale LOCALE = Locale.ENGLISH;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
    @JsonPropertyOrder(value = {"class"}, alphabetic = true)
    private Map<String, Object> operationArgs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private Map<String, String> options = new HashMap<>();

    public Operation(final String id) {
        this.id = id;
    }

    @JsonCreator
    public Operation(@JsonProperty("id") final String id, @JsonProperty("operationArgs") final Map<String, Object> operationArgs, @JsonProperty("options") final Map<String, String> options) {
        this.id = id;
        if (Objects.nonNull(operationArgs)) {
            this.operationArgs = operationArgs;
        }
        if (Objects.nonNull(options)) {
            this.options = options;
        }
    }

    public boolean containsKey(final String key) {
        return operationArgs.containsKey(key);
    }

    public Operation operationArgs(final Map<String, Object> operationsArgs) {
        this.operationArgs = operationsArgs;
        return this;
    }

    public Map<String, Object> getOperationArgs() {
        return operationArgs;
    }

    public Operation addOperationArgs(final Map<String, Object> operationsArgs) {
        this.operationArgs.putAll(operationsArgs);
        return this;
    }

    public Operation operationArg(final String operationArg, final Object value) {
        this.operationArgs.put(operationArg, value);
        return this;
    }

    public Operation inputOperationArg(final Object value) {
        return this.operationArg("input", value);
    }

    public Object get(final String key) {
        return operationArgs.get(key);
    }

    public Operation input(final Object input) {
        return inputOperationArg(input);
    }

    public Object getOrDefault(final String key, final Object defaultValue) {
        return operationArgs.getOrDefault(key, defaultValue);
    }

    public String getId() {
        //TODO case insensitive comparison required.
        return id;
    }

    public Set<String> keySet() {
        return ImmutableSet.copyOf(operationArgs.keySet());
    }

    /**
     * Operation implementations should ensure a ShallowClone method is implemented.
     * Performs a shallow clone. Creates a new instance and copies the fields across.
     * It does not clone the fields.
     * If the operation contains nested operations, these must also be cloned.
     *
     * @return shallow clone
     * @throws CloneFailedException if a Clone error occurs
     */
    public Operation shallowClone() throws CloneFailedException {
        return new Operation(id)
                .operationArgs(operationArgs)
                .options(options);
    }

    /**
     * @return the operation options. This may contain store specific options such as authorisation strings or and
     * other properties required for the operation to be executed. Note these options will probably not be interpreted
     * in the same way by every Executor impl.
     */
    @JsonIgnore
    public Map<String, String> getOptions() {
        return this.options;
    }

    /**
     * @param options the operation options. This may contain Executor specific options such as authorisation strings or and
     *                other properties required for the operation to be executed. Note these options will probably not be interpreted
     *                in the same way by every Executor impl.
     * @return the Operation
     */
    @JsonSetter
    public Operation options(final Map<String, String> options) {
        if (isNull(options)) {
            this.options.clear();
        } else {
            this.options = options;
        }
        return this;
    }

    /**
     * Adds an operation option. This may contain Executor specific options such as authorisation strings or and
     * other properties required for the operation to be executed. Note these options will probably not be interpreted
     * in the same way by every Executor impl.
     *
     * @param name  the name of the option
     * @param value the value of the option
     */
    public Operation option(final String name, final String value) {
        if (null == getOptions()) {
            options(new HashMap<>());
        }

        getOptions().put(name, value);
        return this;
    }

    /**
     * Gets an operation option by its given name.
     *
     * @param name the name of the option
     * @return the value of the option
     */
    public String getOption(final String name) {
        if (null == getOptions()) {
            return null;
        }

        return getOptions().get(name);
    }

    /**
     * Gets an operation option by its given name.
     *
     * @param name         the name of the option
     * @param defaultValue the default value to return if value is null.
     * @return the value of the option
     */
    public String getOption(final String name, final String defaultValue) {
        final String rtn;
        if (null == getOptions()) {
            rtn = defaultValue;
        } else {
            rtn = getOptions().get(name);
        }
        return (null == rtn) ? defaultValue : rtn;
    }

    @JsonGetter("options")
    public Map<String, String> _getNullOrOptions() {
        if (null == getOptions()) {
            return null;
        }

        return getOptions().isEmpty() ? null : getOptions();
    }

    /**
     * Operation implementations should ensure that all closeable fields are closed in this method.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        // do nothing by default
    }

    /**
     * Validates an operation. This should be used to validate that fields have been be configured correctly.
     * By default no validation is applied. Override this method to implement validation.
     *
     * @return validation result.
     */
    public ValidationResult validate() {
        final ValidationResult result = new ValidationResult();

        HashSet<Field> fields = Sets.newHashSet();
        Class<?> currentClass = this.getClass();
        while (null != currentClass) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }

        for (final Field field : fields) {
            final Required[] annotations = field.getAnnotationsByType(Required.class);
            if (null != annotations && annotations.length > 0) {
                if (field.isAccessible()) {
                    validateRequiredFieldPresent(result, field);
                } else {
                    AccessController.doPrivileged((PrivilegedAction<Operation>) () -> {
                        field.setAccessible(true);
                        validateRequiredFieldPresent(result, field);
                        return null;
                    });
                }
            }
        }

        return result;
    }

    public void validateRequiredFieldPresent(final ValidationResult result, final Field field) {
        final Object value;
        try {
            value = field.get(this);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (null == value) {
            result.addError(field.getName() + " is required for: " + this.getClass().getSimpleName());
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

        final Operation that = (Operation) o;

        final EqualsBuilder equalsBuilder = new EqualsBuilder()
                .append(id, that.id)
                .append(options, that.options)
                .append(operationArgs.size(), that.operationArgs.size());


        if (equalsBuilder.isEquals()) {
            boolean mapsAreEqual = true;
            // final boolean mapsAreEqual =
            //         operationArgs.entrySet().stream()
            //                 .allMatch(e -> that.containsKey(e.getKey())
            //                         && ( that.get(e.getKey()).equals(e.getValue()))
            //                 || e.getValue() instanceof Arrays  );


            for (final Map.Entry<String, Object> entry : this.operationArgs.entrySet()) {
                final String thisKey = entry.getKey();
                final boolean b = that.operationArgs.containsKey(thisKey);
                if (!b) {
                    mapsAreEqual = false;
                    break;
                } else {
                    final Object thisValue = entry.getValue();
                    final Object thatValue = operationArgs.get(thisKey);
                    if (!thisValue.equals(thatValue)) {
                        mapsAreEqual = false;
                        break;
                    }
                }
            }


            equalsBuilder.appendSuper(mapsAreEqual);
        }

        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(operationArgs)
                .append(options)
                .toHashCode();
    }
}

