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

package uk.gov.gchq.maestro.operation;

import org.apache.commons.lang3.reflect.TypeUtils;

import uk.gov.gchq.koryphe.ValidationResult;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * Utility methods for handling {@link Operation}s.
 */
public final class OperationUtil {
    /*TODO review this whole Util class*/

    private OperationUtil() {
        // Private constructor to prevent instantiation.
    }

    // public static Class<?> getInputType(final Operation input) {
    //     return getInputType(input.getId());
    // }
    //
    // public static Class<?> getInputType(final Class<? extends Input> input) {
    //     return getType(input, Input.class);
    // }
    //
    // public static Class<?> getOutputType(final Operation output) {
    //     return getOutputType(output.getClass());
    // }
    //
    // public static Class<?> getOutputType(final Operation output) {
    //     return getType(output, output.getId());
    // }
    //
    // public static ValidationResult isValid(final Class<?> output, final Class<?> input) {
    //     ValidationResult result = new ValidationResult();
    //     if (null != input && null != output
    //             && !UnknownGenericType.class.equals(output)
    //             && !UnknownGenericType.class.equals(input)
    //             && !input.isAssignableFrom(output)) {
    //         result.addError("Incompatible output, input types. " + output + ": " + output.getName() + ", input: " + input.getName());
    //     }
    //     return result;
    // }
    //
    // private static Class getType(final Class<? extends Operation> operation, final Class<?> objectClass) {
    //     final TypeVariable<?> tv = objectClass.getTypeParameters()[0];
    //     final Map<TypeVariable<?>, Type> typeArgs = TypeUtils.getTypeArguments(operation, objectClass);
    //     final Type type = typeArgs.get(tv);
    //     return getType(type, typeArgs);
    // }
    //
    // private static Class getType(final Type type, final Map<TypeVariable<?>, Type> typeArgs) {
    //     Type rawType = type;
    //     if (type instanceof ParameterizedType) {
    //         rawType = ((ParameterizedType) type).getRawType();
    //     }
    //
    //     if (rawType instanceof Class) {
    //         return (Class) rawType;
    //     } else {
    //         if (rawType instanceof TypeVariable) {
    //             Type t = typeArgs.get(rawType);
    //             if (null != t) {
    //                 return getType(t, typeArgs);
    //             }
    //         }
    //
    //         return UnknownGenericType.class;
    //     }
    // }
    //
    // public static class UnknownGenericType {
    // }
}
