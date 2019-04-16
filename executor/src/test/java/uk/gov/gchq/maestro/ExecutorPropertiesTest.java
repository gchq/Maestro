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

package uk.gov.gchq.maestro;

import com.fasterxml.jackson.databind.Module;
import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.gov.gchq.koryphe.util.ReflectionUtil;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiserModules;

import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExecutorPropertiesTest {

    @Before
    @After
    public void cleanUp() {
        ReflectionUtil.resetReflectionPackages();
    }

    @Test
    public void shouldMergeProperties() {
        // Given
        final ExecutorProperties props1 = createExecutorProperties();
        final ExecutorProperties props2 = ExecutorProperties.loadExecutorProperties(StreamUtil.openStream(getClass(), "executor2.properties"));

        // When
        props1.merge(props2);

        // Then
        assertEquals("value1", props1.get("key1"));
        assertEquals("value2", props1.get("key2"));
        assertEquals("value2", props1.get("testKey"));
    }

    @Test
    public void shouldRemovePropertyWhenPropertyValueIsNull() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        props.set("testKey", null);

        // Then
        assertNull(props.get("testKey"));
    }

    @Test
    public void shouldGetProperty() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        String value = props.get("key1");

        // Then
        assertEquals("value1", value);
    }

    @Test
    public void shouldSetAndGetProperty() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        props.set("key2", "value2");
        String value = props.get("key2");

        // Then
        assertEquals("value2", value);
    }

    @Test
    public void shouldGetPropertyWithDefaultValue() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        String value = props.get("key1", "property not found");

        // Then
        assertEquals("value1", value);
    }

    @Test
    public void shouldGetUnknownProperty() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        String value = props.get("a key that does not exist");

        // Then
        assertNull(value);
    }

    @Test
    public void shouldAddOperationDeclarationPathsWhenNullExisting() {
        // Given
        final ExecutorProperties props = createExecutorProperties();
        assertNull(props.getOperationDeclarationPaths());

        // When
        props.addOperationDeclarationPaths("1", "2");

        // Then
        assertEquals("1,2", props.getOperationDeclarationPaths());
    }

    @Test
    public void shouldAddOperationDeclarationPathsWhenExisting() {
        // Given
        final ExecutorProperties props = createExecutorProperties();
        props.setOperationDeclarationPaths("1");

        // When
        props.addOperationDeclarationPaths("2", "3");

        // Then
        assertEquals("1,2,3", props.getOperationDeclarationPaths());
    }

    @Test
    public void shouldAddReflectionPackagesToKorypheReflectionUtil() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        props.setReflectionPackages("package1,package2");

        // Then
        assertEquals("package1,package2", props.getReflectionPackages());
        final Set<String> expectedPackages = Sets.newHashSet(ReflectionUtil.DEFAULT_PACKAGES);
        expectedPackages.add("package1");
        expectedPackages.add("package2");
        assertEquals(expectedPackages, ReflectionUtil.getReflectionPackages());
    }

    @Test
    public void shouldGetUnknownPropertyWithDefaultValue() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        String value = props.get("a key that does not exist", "property not found");

        // Then
        assertEquals("property not found", value);
    }

    private ExecutorProperties createExecutorProperties() {
        return ExecutorProperties.loadExecutorProperties(StreamUtil.executorProps(getClass()));
    }

    @Test
    public void shouldSetJsonSerialiserModules() {
        // Given
        final ExecutorProperties props = createExecutorProperties();
        final Set<Class<? extends JSONSerialiserModules>> modules = Sets.newHashSet(
                TestCustomJsonModules1.class,
                TestCustomJsonModules2.class
        );

        // When
        props.setJsonSerialiserModules(modules);

        // Then
        assertTrue(props.getJsonSerialiserModules().contains(TestCustomJsonModules1.class.getName()));
        assertTrue(props.getJsonSerialiserModules().contains(TestCustomJsonModules2.class.getName()));
    }

    @Test
    public void shouldGetAndSetAdminAuth() {
        // Given
        final String adminAuth = "admin auth";
        final ExecutorProperties props = createExecutorProperties();

        // When
        props.setAdminAuth(adminAuth);

        // Then
        assertEquals(adminAuth, props.getAdminAuth());
    }

    public static final class TestCustomJsonModules1 implements JSONSerialiserModules {
        public static List<Module> modules;

        @Override
        public List<Module> getModules() {
            return modules;
        }
    }

    public static final class TestCustomJsonModules2 implements JSONSerialiserModules {
        public static List<Module> modules;

        @Override
        public List<Module> getModules() {
            return modules;
        }
    }
}
