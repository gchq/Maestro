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

package uk.gov.gchq.maestro.helper;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.koryphe.util.SummaryUtil;
import uk.gov.gchq.koryphe.util.VersionUtil;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class MaestroObjectTest<T> {

    @Test
    public void shouldJSONSerialise() throws Exception {
        final T testObject = getTestObject();
        requireNonNull(testObject);
        final String jsonString = getJSONString();
        requireNonNull(jsonString);

        final byte[] serialisedTestObject = JSONSerialiser.serialise(testObject, true);
        assertNotNull("serialised testObject is null", serialisedTestObject);
        assertEquals("json strings are not equal, between serialisedTestObject and jsonString", jsonString, new String(serialisedTestObject));
        final T deserialise = JSONSerialiser.deserialise(serialisedTestObject, getTestObjectClass());
        assertNotNull("deserialised testObject is null", deserialise);
        assertEquals("deserialised object is not the same as testObject", testObject, deserialise);
    }

    @Test
    public void shouldShallowCloneOperation() throws Exception {
        final T testObject = getTestObject();
        if (testObject instanceof Operation) {
            assertEquals("testObject.shallowClone() is not equal to original testObject", testObject, ((Operation) testObject).shallowClone());
        }
    }

    @Test
    public void shouldSetGetOperationOption() throws Exception {
        final T testObject = getTestObject();
        if (testObject instanceof Operation) {
            final Operation testOperation = (Operation) testObject;
            final HashMap<String, String> expected = Maps.newHashMap();
            expected.put("one", "two");
            testOperation.options(expected);
            final Map<String, String> actual = testOperation.getOptions();
            Assert.assertEquals(expected, actual);
            assertEquals("two", testOperation.getOption("one"));
        }
    }

    @Test
    public void shouldNotSetNullOptions() throws Exception {
        final T testObject = getTestObject();
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
    public void shouldHaveJsonPropertyAnnotation() throws Exception {
        // Given
        final T op = getTestObject();

        // When
        final JsonPropertyOrder annotation = op.getClass().getAnnotation(JsonPropertyOrder.class);

        // Then
        assertTrue("Missing JsonPropertyOrder annotation on class. It should de defined and set to alphabetical." + op.getClass().getName(),
                null != annotation && annotation.alphabetic());
    }

    @Test
    public void shouldHaveSinceAnnotation() throws Exception {
        // Given
        final T testObject = getTestObject();
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
    public void shouldHaveSummaryAnnotation() throws Exception {
        // Given
        final T testObject = getTestObject();
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

    protected abstract T getTestObject() throws Exception;
}
