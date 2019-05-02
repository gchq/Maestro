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

package uk.gov.gchq.maestro.rest.factory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.CommonTestConstants;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.rest.SystemProperty;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class ExecutorFactoryTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);

    @Before
    @After
    public void cleanUp() {
        System.clearProperty(SystemProperty.MAESTRO_CONFIG_PATH);
    }

    @Test
    public void shouldCreateDefaultGraphFactoryWhenNoSystemProperty() {
        // Given

        // When
        final Executor executor = ExecutorFactory.createExecutor();

        // Then
        assertEquals(Executor.class, executor.getClass());
    }

    @Test
    public void shouldCreateExecutor() throws IOException {
        // Given
        final File configFile = testFolder.newFile("config.json");
        FileUtils.writeLines(configFile, IOUtils.readLines(StreamUtil.openStream(getClass(), "config.json")));

        System.setProperty(SystemProperty.MAESTRO_CONFIG_PATH, configFile.getAbsolutePath());

        final ExecutorFactory factory = new ExecutorFactory();

        // When
        final Executor executor = factory.createExecutor();

        // Then
        assertEquals("id1", executor.getConfig().getId());

    }
}
