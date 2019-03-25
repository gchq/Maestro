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

import uk.gov.gchq.maestro.ExecutorProperties;
import uk.gov.gchq.maestro.commonutil.ToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@code HashMapLibrary} stores a {@link Library} within a HashMap.
 */
public class HashMapLibrary extends Library {
    private static final Map<String, String> EXECUTORS = new HashMap<>();
    private static final Map<String, ExecutorProperties> PROPERTIES = new HashMap<>();

    public static void clear() {
        EXECUTORS.clear();
        PROPERTIES.clear();
    }

    @Override
    public void initialise(final String path) {
        // Do nothing
    }

    @Override
    public String getPropertiesId(final String executorId) {
        return EXECUTORS.get(executorId);
    }

    @Override
    protected void _addId(final String executorId, final String propsId) {
        EXECUTORS.put(executorId, propsId);
    }

    @Override
    protected void _addProperties(final String propertiesId, final ExecutorProperties properties) {
        PROPERTIES.put(propertiesId, properties);
    }

    @Override
    protected ExecutorProperties _getProperties(final String propertiesId) {
        final ExecutorProperties executorProperties = PROPERTIES.get(propertiesId);
        return (null == executorProperties) ? null : executorProperties.clone();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("executors", EXECUTORS)
                .toString();
    }
}
