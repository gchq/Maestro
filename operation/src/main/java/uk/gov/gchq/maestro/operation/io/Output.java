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

package uk.gov.gchq.maestro.operation.io;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

import uk.gov.gchq.maestro.operation.Operation;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * {@code Output} operations are Maestro operations which yield an output.
 *
 * @param <O> the type of output object
 */
public interface Output<O> extends Operation {
    default O castToOutputType(final Object result) {
        try {
            return (O) result;
        } catch (final ClassCastException e) {
            final Class<?> resultClass = null != result ? result.getClass() : null;
            throw new IllegalArgumentException("Operation result is an invalid type: " + resultClass, e);
        }
    }

    @JsonIgnore
    TypeReference<O> getOutputTypeReference();

    @JsonIgnore
    default Class<?> getOutputClass() {
        Class<?> outputClass = Object.class;

        final TypeReference<O> outputType = getOutputTypeReference();
        if (null != outputType) {
            Type type = outputType.getType();
            if (type instanceof ParameterizedType) {
                type = ((ParameterizedType) type).getRawType();
            }
            if (type instanceof Class) {
                outputClass = (Class) type;
            }
        }

        return outputClass;
    }

    interface Builder<OP extends Output<O>, O, B extends Builder<OP, O, ?>> extends Operation.Builder<OP, B> {
    }
}
