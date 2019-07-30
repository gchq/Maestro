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

package uk.gov.gchq.maestro.operation.impl;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;

import java.util.HashMap;

import static java.util.Objects.nonNull;

@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class HashMapHandler implements OperationHandler {
    public static final String MAP_COMMAND = "mapCommand";
    public static final String MAP_KEY = "mapKey";
    public static final String MAP_VALUE = "mapValue";
    public static final String MAP_GET_KEY = "mapGetKey";
    public static final String COMMAND_PUT = "put";
    public static final String COMMAND_GET = "get";
    HashMap<Object, Object> delegateMap = new HashMap<>();

    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        final String command = (String) operation.getOrDefault(MAP_COMMAND, "null");
        Object rtn = null; //TODO? new TypeReferenceImpl.Void()
        switch (command) {
            case COMMAND_PUT: {
                final Object key = operation.get(MAP_KEY);
                if (nonNull(key)) {
                    final Object value = operation.get(MAP_VALUE);
                    delegateMap.put(key, value);
                }
                break;
            }
            case COMMAND_GET: {
                final Object key = operation.get(MAP_KEY);
                if (nonNull(key)) {
                    rtn = delegateMap.get(key);
                }
                break;
            }
            default: {
                throw new OperationException(new UnsupportedOperationException("map command not supported: " + command));
            }
        }

        return rtn;
    }

    public HashMap<Object, Object> getDelegateMap() {
        return delegateMap;
    }

    public HashMapHandler setDelegateMap(final HashMap<Object, Object> delegateMap) {
        this.delegateMap = delegateMap;
        return this;
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration()
                .fieldOptional(MAP_KEY, Object.class)
                .fieldOptional(MAP_VALUE, Object.class)
                .fieldOptional(MAP_GET_KEY, Object.class)
                .field(MAP_COMMAND, String.class);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final HashMapHandler that = (HashMapHandler) o;

        return new EqualsBuilder()
                .append(delegateMap, that.delegateMap)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(delegateMap)
                .toHashCode();
    }
}
