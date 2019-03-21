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

import uk.gov.gchq.maestro.StoreProperties;
import uk.gov.gchq.maestro.util.Config;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class AbstractLibraryTest {

    protected Library library;

    private static final String TEST_EXECUTOR_ID = "testExecutorId";
    private static final String TEST_EXECUTOR_ID_1 = "testExecutorId1";
    private static final String TEST_UNKNOWN_ID = "unknownId";

    private static final String EXCEPTION_EXPECTED = "Exception expected";

    private StoreProperties storeProperties = new StoreProperties();
    private StoreProperties storeProperties1 = new StoreProperties();
    private Config config =
            new Config.Builder().storeProperties(storeProperties).build();
    private Config config1 =
            new Config.Builder().storeProperties(storeProperties1).build();

    public abstract Library createLibraryInstance();

    @Before
    public void beforeEach() {
        library = createLibraryInstance();
        if (library instanceof HashMapLibrary) {
            HashMapLibrary.clear();
        }
    }

    @Test
    public void shouldAddAndGetMultipleIdsInGraphLibrary() {
        // When
        library.addConfig(TEST_EXECUTOR_ID, config);
        library.addConfig(TEST_EXECUTOR_ID_1,
                config1);

        assertEquals(config, library.getConfig(TEST_EXECUTOR_ID));
        assertEquals(config1, library.getConfig(TEST_EXECUTOR_ID_1));
    }

    @Test
    public void shouldAddAndGetIdsInGraphLibrary() {
        // When
        library.addConfig(TEST_EXECUTOR_ID, config);

        // Then
        assertEquals(config, library.getConfig(TEST_EXECUTOR_ID));
    }

    @Test
    public void shouldThrowExceptionWithInvalidGraphId() {
        // When / Then
        try {
            library.addConfig(TEST_EXECUTOR_ID + "@#", config);
            fail(EXCEPTION_EXPECTED);
        } catch (final IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldUpdateWhenGraphIdExists() {
        // When
        library.addConfig(TEST_EXECUTOR_ID, config);

        // Then
        assertEquals(config, library.getConfig(TEST_EXECUTOR_ID));

        // When
        library.addConfig(TEST_EXECUTOR_ID, config1);

        // Then
        assertEquals(config1, library.getConfig(TEST_EXECUTOR_ID));
    }
}

