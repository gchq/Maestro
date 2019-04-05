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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.commonutil.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * A {@code ExecutorProperties} contains specific configuration information
 * for the executor, such as database
 * connection strings. It wraps {@link Properties} and lazy loads the all properties from a file when first used.
 * <p>
 * All ExecutorProperties classes must be JSON serialisable.
 * </p>
 */
public class ExecutorProperties extends Properties {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorProperties.class);

    // Required for loading by reflection.
    public ExecutorProperties() {
        super();
    }

    public ExecutorProperties(final Path propFileLocation) {
        super();
        if (null != propFileLocation) {
            try {
                loadExecutorProperties(null != propFileLocation ? Files.newInputStream(propFileLocation) : null);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ExecutorProperties(final Properties props) {
        super();
        putAll(props);
    }

    public ExecutorProperties(final String pathStr) {
        super();
        final Path path = Paths.get(pathStr);
        try {
            if (path.toFile().exists()) {
                loadExecutorProperties(Files.newInputStream(path));
            } else {
                loadExecutorProperties(StreamUtil.openStream(ExecutorProperties.class, pathStr));
            }
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load executor properties " +
                    "file : " + e.getMessage(), e);
        }
    }

    public ExecutorProperties loadExecutorProperties(final InputStream executorPropertiesStream) {
        if (null == executorPropertiesStream) {
            return new ExecutorProperties();
        }
        final Properties props = new Properties();
        try {
            props.load(executorPropertiesStream);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load executor properties file : " + e.getMessage(), e);
        } finally {
            try {
                executorPropertiesStream.close();
            } catch (final IOException e) {
                LOGGER.error("Failed to close executor properties stream: {}", e.getMessage(), e);
            }
        }
        return loadExecutorProperties(props);
    }

    public ExecutorProperties loadExecutorProperties(final Properties props) {
        putAll(props);
        return this;
    }

    @Override
    public Object setProperty(final String key, final String value) {
        if (null == value) {
            return remove(key);
        } else {
            return super.setProperty(key, value);
        }
    }

    public void merge(final ExecutorProperties properties) {
        if (null != properties) {
            this.putAll(properties);
        }
    }

    @Override
    public ExecutorProperties clone() {
        return (ExecutorProperties) super.clone();
    }
}
