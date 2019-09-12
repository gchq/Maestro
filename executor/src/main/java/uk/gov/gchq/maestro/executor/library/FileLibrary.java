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

package uk.gov.gchq.maestro.executor.library;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.io.FileUtils;

import uk.gov.gchq.maestro.executor.util.ExecutorPropertiesUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A {@code FileLibrary} stores a {@link Library} in a specified
 * location as files.  It will store a executorId file with the relationships
 * between the executorId and ExecutorPropertiesId.  It will also store the
 * ExecutorProperties in another file.  This will be named using the id.
 */
public class FileLibrary extends Library {
    private static final Pattern PATH_ALLOWED_CHARACTERS = Pattern.compile("[a-zA-Z0-9_/\\\\\\-]*");
    private static final String DEFAULT_PATH = "library";
    private String path;

    public FileLibrary() {
        this(DEFAULT_PATH);
    }

    public FileLibrary(final String path) {
        setPath(path);
    }

    @Override
    public void initialise(final String path) {
        setPath(path);
    }

    @Override
    public String getPropertiesId(final String executorId) {
        final String propertiesId;
        if (getExecutorsPath(executorId).toFile().exists()) {
            try {
                List<String> lines = Files.readAllLines(getExecutorsPath(executorId));
                propertiesId = lines.get(0);
            } catch (final IOException e) {
                throw new IllegalArgumentException("Could not read executor " +
                        "file: " + getExecutorsPath(executorId), e);
            }
        } else {
            return null;
        }
        return propertiesId;
    }

    @Override
    protected void _addId(final String executorId, final String propsId) {
        try {
            FileUtils.writeStringToFile(getExecutorsPath(executorId).toFile(),
                    propsId);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Could not write Executor to " +
                    "path: " + getExecutorsPath(executorId), e);
        }
    }

    @JsonIgnore
    public String getPath() {
        return path;
    }

    @JsonIgnore
    public void setPath(final String path) {
        if (null == path) {
            this.path = DEFAULT_PATH;
        } else {
            if (!PATH_ALLOWED_CHARACTERS.matcher(path).matches()) {
                throw new IllegalArgumentException("path is invalid: " + path + " it must match the regex: " + PATH_ALLOWED_CHARACTERS);
            }
            this.path = path;
        }
    }

    @Override
    protected void _addProperties(final String propertiesId,
                                  final Map<String, Object> properties) {
        if (null != properties) {
            getPropertiesPath(propertiesId).toFile().getParentFile().mkdirs();
            ExecutorPropertiesUtil.saveProperties(propertiesId, properties, getPropertiesPath(propertiesId));
        } else {
            throw new IllegalArgumentException("ExecutorProperties cannot be " +
                    "null");
        }
    }

    @Override
    protected Map<String, Object> _getProperties(final String propertiesId) {
        final Path propertiesPath = getPropertiesPath(propertiesId);
        if (!propertiesPath.toFile().exists()) {
            return null;
        }
        return ExecutorPropertiesUtil.loadProperties(propertiesPath);
    }

    private Path getPropertiesPath(final String propertiesId) {
        return Paths.get(path + "/" + propertiesId + "Props.properties");
    }

    private Path getExecutorsPath(final String executorId) {
        return Paths.get(path + "/" + executorId + "Executors.json");
    }
}
