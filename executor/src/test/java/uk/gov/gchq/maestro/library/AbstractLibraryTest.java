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

package uk.gov.gchq.maestro.library;


import org.junit.Before;
import org.junit.Test;

import uk.gov.gchq.maestro.util.Config;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public abstract class AbstractLibraryTest {

    protected Library library;

    private static final String TEST_EXECUTOR_ID = "testExecutorId";
    private static final String TEST_EXECUTOR_ID_1 = "testExecutorId1";

    private static final String EXCEPTION_EXPECTED = "Exception expected";

    private Properties executorProperties = new Properties();
    private Properties executorProperties1 = new Properties();
    private Config config =
            new Config.Builder().executorProperties(executorProperties).build();
    private Config config1 =
            new Config.Builder().executorProperties(executorProperties1).build();

    public abstract Library createLibraryInstance();

    @Before
    public void beforeEach() {
        library = createLibraryInstance();
        if (library instanceof HashMapLibrary) {
            HashMapLibrary.clear();
        }
    }

    @Test
    public void shouldAddAndGetMultipleIdsInExecutorLibrary() {
        // When
        library.addProperties(TEST_EXECUTOR_ID, executorProperties);
        library.addProperties(TEST_EXECUTOR_ID_1, executorProperties1);

        assertEquals(executorProperties, library.getPropertiesUsingPropertiesId(TEST_EXECUTOR_ID));
        assertEquals(executorProperties1, library.getPropertiesUsingPropertiesId(TEST_EXECUTOR_ID_1));
    }

    @Test
    public void shouldThrowExceptionWithInvalidExecutorId() {
        // When / Then
        try {
            library.add(TEST_EXECUTOR_ID + "@#", executorProperties);
            fail(EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldUpdateWhenExecutorIdExists() {
        // When
        library.addProperties(TEST_EXECUTOR_ID, executorProperties);

        // Then
        assertEquals(executorProperties, library.getPropertiesUsingPropertiesId(TEST_EXECUTOR_ID));

        // When
        library.addProperties(TEST_EXECUTOR_ID, executorProperties1);

        // Then
        assertEquals(executorProperties1, library.getPropertiesUsingPropertiesId(TEST_EXECUTOR_ID));
    }
}

