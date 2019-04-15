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


import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertNull;

public class HashMapLibraryTest extends AbstractLibraryTest {

    private static final String TEST_ID = "testId";

    @Override
    public Library createLibraryInstance() {
        return new HashMapLibrary();
    }

    @Test
    public void shouldClearLibrary() {
        // When
        final HashMapLibrary library = new HashMapLibrary();
        library.addProperties(TEST_ID, new Properties());
        library.clear();

        // Then
        assertNull(library.getPropertiesUsingPropertiesId(TEST_ID));
    }
}
