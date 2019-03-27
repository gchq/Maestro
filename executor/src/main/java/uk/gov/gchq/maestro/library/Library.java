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
import uk.gov.gchq.maestro.commonutil.exception.OverwritingException;

import java.util.regex.Pattern;

/**
 * A Library contains executorIds and their related {@link ExecutorProperties}.
 */
public abstract class Library {
    protected static final Pattern ID_ALLOWED_CHARACTERS = Pattern.compile("[a-zA-Z0-9_]*");
    public static final String A_LIBRARY_CAN_T_BE_ADDED_WITH_A_NULL_S_ID_S = "A Library can't be added with a null %s, Id: %s";

    public abstract void initialise(final String path);

    /**
     * Add a new relationship between a executorId and StoreProperties.
     *
     * @param executorId The executorId to relate to.
     * @param properties The StoreProperties that relate to the executorId.
     * @throws OverwritingException If the executorId already has related StoreProperties.
     */
    public void add(final String executorId, final ExecutorProperties properties) throws OverwritingException {
        add(executorId, executorId, properties);
    }

    /**
     * Add a new relationship between a executorId and {@link ExecutorProperties}.
     *
     * @param executorId   The executorId to relate to.
     * @param propertiesId the properties id
     * @param properties   The ExecutorProperties that relate to the executorId.
     * @throws OverwritingException If the executorId already has related
     *                              ExecutorProperties.
     */
    public void add(final String executorId,
                    final String propertiesId, final ExecutorProperties properties) throws OverwritingException {
        validateId(executorId);
        checkExisting(executorId, properties);

        nullCheck(executorId, properties);

        String resolvedPropertiesId = null != propertiesId ? propertiesId : executorId;
        addProperties(resolvedPropertiesId, properties);
        _addId(executorId, resolvedPropertiesId);
    }

    /**
     * Adds a new relationship between a executorId and ExecutorProperties.
     * If there is already a relationship using the executorId, it will update it.
     *
     * @param executorId The executorId to relate to.
     * @param properties The ExecutorProperties that relate to the executorId.
     */
    public void addOrUpdate(final String executorId, final ExecutorProperties properties) {
        addOrUpdate(executorId, executorId, properties);
    }

    /**
     * Adds a new relationship between a executorId and ExecutorProperties.
     * If there is already a relationship using the executorId, it will update it.
     *
     * @param executorId   The executorId to relate to.
     * @param propertiesId the properties id
     * @param properties   The ExecutorProperties that relate to the executorId.
     */
    public void addOrUpdate(final String executorId, final String propertiesId,
                            final ExecutorProperties properties) {
        validateId(executorId);

        nullCheck(executorId, properties);

        String resolvedPropertiesId = null != propertiesId ? propertiesId : executorId;
        _addProperties(resolvedPropertiesId, properties);

        _addId(executorId, resolvedPropertiesId);
    }

    /**
     * Gets the StoreProperties related to the executorId.
     *
     * @param executorId The executorId.
     * @return ExecutorProperties.
     */
    public ExecutorProperties getPropertiesUsingExecutorId(final String executorId) {
        validateId(executorId);

        final String propsId = getPropertiesId(executorId);
        if (null == propsId) {
            return null;
        }

        return _getProperties(propsId);
    }

    /**
     * Gets the Schema Id and StoreProperties Id related to the executorId.
     *
     * @param executorId The executorId.
     * @return ExecutorPropertiesId
     */
    public abstract String getPropertiesId(final String executorId);

    /**
     * Gets the StoreProperties given the storePropertiesId.
     *
     * @param propertiesId The storePropertiesId
     * @return The {@link ExecutorProperties} related to the storePropertiesId.
     */
    public ExecutorProperties getPropertiesUsingPropertiesId(final String propertiesId) {
        validateId(propertiesId);

        return _getProperties(propertiesId);
    }

    /**
     * Checks if the executorId with a relationship already exists.
     *
     * @param executorId The executorId.
     * @return True if a relationship exists.
     */
    public boolean checkPropertiesExist(final String executorId) {
        return null != getPropertiesId(executorId);
    }

    /**
     * Adds a new relationship between a ExecutorProperties and a
     * ExecutorPropertiesId.
     *
     * @param id         the properties ID.
     * @param properties the ExecutorProperties.
     * @throws OverwritingException If there is already a relationship.
     */
    public void addProperties(final String id, final ExecutorProperties properties) throws OverwritingException {
        if (null != properties) {
            validateId(id);
            if (!checkPropertiesExist(id, properties)) {
                _addProperties(id, properties);
            }
        }
    }

    /**
     * Adds a new relationship between a ExecutorProperties and a
     * executorPropertiesId.
     * If there is already an existing relationship, it will update it.
     *
     * @param id         the properties ID.
     * @param properties the ExecutorProperties.
     */
    public void addOrUpdateProperties(final String id, final ExecutorProperties properties) {
        if (null != properties) {
            validateId(id);
            _addProperties(id, properties);
        }
    }

    public ExecutorProperties resolveExecutorProperties(final ExecutorProperties properties,
                                                        final String parentExecutorPropertiesId) {
        ExecutorProperties resultProps = null;
        if (null != parentExecutorPropertiesId) {
            resultProps = this.getPropertiesUsingPropertiesId(parentExecutorPropertiesId);
        }
        if (null != properties) {
            if (null == resultProps) {
                resultProps = properties;
            } else {
                resultProps.merge(properties);
            }
        }
        return resultProps;
    }

    protected abstract void _addId(final String executorId, final String propsId);

    protected abstract void _addProperties(final String propertiesId, final ExecutorProperties properties);

    protected abstract ExecutorProperties _getProperties(final String propertiesId);

    private void validateId(final String id) {
        if (null == id || !ID_ALLOWED_CHARACTERS.matcher(id).matches()) {
            throw new IllegalArgumentException("Id is invalid: " + id + ", it must match regex: " + ID_ALLOWED_CHARACTERS);
        }
    }

    private void checkExisting(final String executorId, final ExecutorProperties properties) {
        final ExecutorProperties existingExecutorProps =
                getPropertiesUsingExecutorId(executorId);

        if (null != existingExecutorProps) {
            if (existingExecutorProps.getProperties().equals(properties.getProperties())) {
                throw new OverwritingException("ExecutorId " + executorId +
                        " already " +
                        "exists with a different executor properties:\n"
                        + "existing executorProperties:\n" + existingExecutorProps.toString()
                        + "\nnew executorProperties:\n" + properties.toString());
            }
        }
    }

    private boolean checkPropertiesExist(final String id, final ExecutorProperties properties) {
        final ExecutorProperties existingProperties = _getProperties(id);
        final boolean exists = null != existingProperties;
        if (exists) {
            if (!existingProperties.getProperties().equals(properties.getProperties())) {
                throw new OverwritingException("propertiesId " + id + " " +
                        "already exists with a different executor properties:\n"
                        + "existing executorProperties:\n" + existingProperties.getProperties().toString()
                        + "\nnew executorProperties:\n" + properties.getProperties().toString());
            }
        }
        return exists;
    }

    private void nullCheck(final String executorId, final ExecutorProperties properties) {
        if (null == properties) {
            throw new IllegalArgumentException(String.format(A_LIBRARY_CAN_T_BE_ADDED_WITH_A_NULL_S_ID_S, ExecutorProperties.class.getSimpleName(), executorId));
        }
    }
}
