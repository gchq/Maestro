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
import uk.gov.gchq.maestro.util.ExecutorPropertiesUtil;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
        final ExecutorProperties props2 = new ExecutorProperties().loadExecutorProperties(StreamUtil.openStream(getClass(),
                "executor2.properties"));

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
        props.setProperty("testKey", null);

        // Then
        assertNull(props.get("testKey"));
    }

    @Test
    public void shouldGetProperty() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        String value = (String) props.get("key1");

        // Then
        assertEquals("value1", value);
    }

    @Test
    public void shouldSetAndGetProperty() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        props.setProperty("key2", "value2");
        String value = (String) props.get("key2");

        // Then
        assertEquals("value2", value);
    }

    @Test
    public void shouldGetPropertyWithDefaultValue() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        String value = props.getProperty("key1", "property not found");

        // Then
        assertEquals("value1", value);
    }

    @Test
    public void shouldGetUnknownProperty() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        String value = (String) props.get("a key that does not exist");

        // Then
        assertNull(value);
    }

    @Test
    public void shouldAddOperationDeclarationPathsWhenNullExisting() {
        // Given
        final ExecutorProperties props = createExecutorProperties();
        assertNull(ExecutorPropertiesUtil.getOperationDeclarationPaths(props));

        // When
        ExecutorPropertiesUtil.addOperationDeclarationPaths(props, "1", "2");

        // Then
        assertEquals("1,2", ExecutorPropertiesUtil.getOperationDeclarationPaths(props));
    }

    @Test
    public void shouldAddOperationDeclarationPathsWhenExisting() {
        // Given
        final ExecutorProperties props = createExecutorProperties();
        ExecutorPropertiesUtil.setOperationDeclarationPaths(props, "1");

        // When
        ExecutorPropertiesUtil.addOperationDeclarationPaths(props, "2", "3");

        // Then
        assertEquals("1,2,3", ExecutorPropertiesUtil.getOperationDeclarationPaths(props));
    }

    @Test
    public void shouldAddReflectionPackagesToKorypheReflectionUtil() {
        // Given
        final ExecutorProperties props = createExecutorProperties();

        // When
        ExecutorPropertiesUtil.setReflectionPackages(props, "package1,package2");

        // Then
        assertEquals("package1,package2", ExecutorPropertiesUtil.getReflectionPackages(props));
        final Set<String> expectedPackages = Sets.newHashSet(ReflectionUtil.DEFAULT_PACKAGES);
        expectedPackages.add("package1");
        expectedPackages.add("package2");
        assertEquals(expectedPackages, ReflectionUtil.getReflectionPackages());
    }

    private ExecutorProperties createExecutorProperties() {
        return new ExecutorProperties().loadExecutorProperties(StreamUtil.executorProps(getClass()));
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
        ExecutorPropertiesUtil.setJsonSerialiserModules(props, modules);

        // Then
        assertTrue(ExecutorPropertiesUtil.getJsonSerialiserModules(props).contains(TestCustomJsonModules1.class.getName()));
        assertTrue(ExecutorPropertiesUtil.getJsonSerialiserModules(props).contains(TestCustomJsonModules2.class.getName()));
    }

    @Test
    public void shouldGetAndSetAdminAuth() {
        // Given
        final String adminAuth = "admin auth";
        final ExecutorProperties props = createExecutorProperties();

        // When
        ExecutorPropertiesUtil.setAdminAuth(props, adminAuth);

        // Then
        assertEquals(adminAuth, ExecutorPropertiesUtil.getAdminAuth(props));
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
