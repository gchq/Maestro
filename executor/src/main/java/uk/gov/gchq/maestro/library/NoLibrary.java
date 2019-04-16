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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class NoLibrary extends Library {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoLibrary.class);

    public NoLibrary() {
        LOGGER.debug("Nothing will be stored in your library. You will need " +
                "to provide it each time you create an instance of an Executor.");
    }

    @Override
    public void initialise(final String path) {
        // Do nothing
    }

    @Override
    public String getPropertiesId(final String executorId) {
        return null;
    }

    @Override
    protected void _addId(final String executorId, final String propsId) {
        // do nothing
    }

    @Override
    protected void _addProperties(final String propertiesId,
                                  final Properties properties) {
        // do nothing
    }

    @Override
    protected Properties _getProperties(final String propertiesId) {
        return null;
    }
}
