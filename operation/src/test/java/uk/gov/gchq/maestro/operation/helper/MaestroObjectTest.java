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

package uk.gov.gchq.maestro.operation.helper;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.koryphe.util.SummaryUtil;
import uk.gov.gchq.koryphe.util.VersionUtil;
import uk.gov.gchq.maestro.commonutil.Required;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.Operation;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.gchq.maestro.operation.Operation.LOCALE;

public abstract class MaestroObjectTest<T> {

    @Test
    public final void shouldJSONSerialise() throws Exception {
        final T testObject = getFullyPopulatedTestObject();
        requireNonNull(testObject);
        final String jsonString = getJSONString();
        requireNonNull(jsonString);

        final byte[] serialisedTestObject = JSONSerialiser.serialise(testObject, true);
        assertNotNull("serialised testObject is null", serialisedTestObject);
        assertEquals("json strings are not equal, between serialisedTestObject and jsonString", jsonString, new String(serialisedTestObject));
        final T deserialise = JSONSerialiser.deserialise(serialisedTestObject, getTestObjectClass());
        assertNotNull("deserialised testObject is null", deserialise);
        assertEquals("deserialised object does not equal the original testObject", testObject, deserialise);

        final T deserialiseAlt = (T) JSONSerialiser.deserialise(jsonString, this.getClass().getClassLoader());
        assertEquals(testObject, deserialiseAlt);
    }

    protected Set<String> getRequiredFields() {
        return Collections.emptySet();
    }

    @Test
    public final void shouldShallowCloneOperation() throws Exception {
        final T testObject = getFullyPopulatedTestObject();
        if (testObject instanceof Operation) {
            assertEquals("testObject.shallowClone() is not equal to original testObject", testObject, ((Operation) testObject).shallowClone());
        }
    }

    @Test
    public final void shouldSetGetOperationOption() throws Exception {
        final T testObject = getFullyPopulatedTestObject();
        if (testObject instanceof Operation) {
            final Operation testOperation = (Operation) testObject;
            final HashMap<String, String> expected = Maps.newHashMap();
            expected.put("one", "two");
            testOperation.options(expected);
            final Map<String, String> actual = testOperation.getOptions();
            Assert.assertEquals("returned options not equal to expected", expected, actual);
            assertEquals("two", testOperation.getOption("one"));
            final Map<String, String> clonedOptions = testOperation.shallowClone().getOptions();
            assertEquals(actual, clonedOptions);
        }
    }

    @Test
    public final void shouldNotSetNullOptions() throws Exception {
        final T testObject = getFullyPopulatedTestObject();
        if (testObject instanceof Operation) {
            final Operation testOperation = (Operation) testObject;
            final HashMap<String, String> initial = Maps.newHashMap();
            initial.put("one", "two");
            testOperation.options(initial);
            testOperation.options(null);
            final Map<String, String> options = testOperation.getOptions();
            Assert.assertNotNull("options should not be set to null", options);
            assertEquals("options should have been emptied, instead of being set to null", 0, options.size());
        }
    }

    @Test
    public final void shouldHaveJsonPropertyAnnotation() throws Exception {
        // Given
        final T op = getFullyPopulatedTestObject();

        // When
        final JsonPropertyOrder annotation = op.getClass().getAnnotation(JsonPropertyOrder.class);

        // Then
        assertTrue("Missing JsonPropertyOrder annotation on class. It should de defined and set to alphabetical." + op.getClass().getName(),
                null != annotation && annotation.alphabetic());
    }

    @Test
    public final void shouldHaveSinceAnnotation() throws Exception {
        // Given
        final T testObject = getFullyPopulatedTestObject();
        if (testObject instanceof Operation) {
            // When
            final Since annotation = testObject.getClass().getAnnotation(Since.class);

            // Then
            if (null == annotation || null == annotation.value()) {
                throw new AssertionError("Missing Since annotation on class " + testObject.getClass().getName());
            }
            assertTrue(annotation.value() + " is not a valid value string.",
                    VersionUtil.validateVersionString(annotation.value()));
        }
    }

    @Test
    public final void shouldHaveSummaryAnnotation() throws Exception {
        // Given
        final T testObject = getFullyPopulatedTestObject();
        if (testObject instanceof Operation) {
            // When
            final Summary annotation = testObject.getClass().getAnnotation(Summary.class);

            // Then
            if (null == annotation || null == annotation.value()) {
                throw new AssertionError("Missing Summary annotation on class " + testObject.getClass().getName());
            }
            assertTrue(annotation.value() + " is not a valid value string.",
                    SummaryUtil.validateSummaryString(annotation.value()));
        }
    }

    protected abstract Class<T> getTestObjectClass();

    protected abstract String getJSONString();

    protected abstract T getFullyPopulatedTestObject() throws Exception;

    @Test
    public final void shouldValidateRequiredFields() throws Exception {
        // Given
        final T testObject = getFullyPopulatedTestObject();
        if (testObject instanceof Operation) {
            final Set<String> lowerCaseArgs = ((Operation) testObject).keySet().stream()
                    .map(s -> s.toLowerCase(LOCALE))
                    .collect(Collectors.toSet());
            final Set<String> requiredFields = getRequiredFields();

            final Set<String> requiredFieldsErrors = requiredFields.stream()
                    .filter(s -> !lowerCaseArgs.contains(s.toLowerCase(LOCALE)))
                    .map(f -> f + " is required for: " + testObject.getClass().getSimpleName())
                    .collect(Collectors.toSet());

            assertTrue(requiredFieldsErrors.toString(), requiredFieldsErrors.isEmpty());

        } else {
            // When
            final ValidationResult validationResult = validate(testObject);

            // Then
            final Set<String> requiredFields = getRequiredFields();
            final Set<String> requiredFieldsErrors = requiredFields.stream()
                    .map(f -> f + " is required for: " + testObject.getClass().getSimpleName())
                    .collect(Collectors.toSet());

            assertEquals(
                    requiredFieldsErrors,
                    validationResult.getErrors()
            );
        }
    }

    private ValidationResult validate(final Object op) {
        final ValidationResult result = new ValidationResult();

        HashSet<Field> fields = Sets.newHashSet();
        Class<?> opClass = op.getClass();
        while (null != opClass) {
            fields.addAll(Arrays.asList(opClass.getDeclaredFields()));
            opClass = opClass.getSuperclass();
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

    private void validateRequiredFieldPresent(final ValidationResult result, final Field field) {
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

    @Test
    public void shouldFindAllArgKeysInJsonString() throws Exception {
        final T fullyPopulatedTestObject = getFullyPopulatedTestObject();
        if (fullyPopulatedTestObject instanceof Operation) {
            final Operation operation = (Operation) fullyPopulatedTestObject;
            final Set<String> keys = operation.keySet();
            final String jsonString = getJSONString().toLowerCase(LOCALE);
            for (String key : keys) {
                assertTrue(String.format("Key: %s not found in jsonString", key), jsonString.contains(key.toLowerCase(LOCALE)));
            }
        }
    }

    @Test
    public void shouldHaveNewInstanceOfFullyPopulatedTestObject() throws Exception {
        final T a = getFullyPopulatedTestObject();
        final T b = getFullyPopulatedTestObject();
        assertEquals("getFullyPopulatedTestObject() does not return equal objects", a, b);
        assertFalse("getFullyPopulatedTestObject() does not return a new instance, this is a sanitary practice for testing", a == b);
    }

    protected byte[] toJson(final T testObj) {
        try {
            return JSONSerialiser.serialise(testObj, true);
        } catch (final SerialisationException e) {
            throw new RuntimeException(e);
        }
    }

    protected T fromJson(final byte[] jsonObj) {
        try {
            return JSONSerialiser.deserialise(jsonObj, getTestObjectClass());
        } catch (final SerialisationException e) {
            throw new RuntimeException(e);
        }
    }

    protected T fromJson(final String path) {
        try {
            return JSONSerialiser.deserialise(StreamUtil.openStream(getClass(), path), getTestObjectClass());
        } catch (final SerialisationException e) {
            throw new RuntimeException(e);
        }
    }
}
