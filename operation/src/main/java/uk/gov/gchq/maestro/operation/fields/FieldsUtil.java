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

package uk.gov.gchq.maestro.operation.fields;

import uk.gov.gchq.maestro.commonutil.exception.MaestroNullPointerException;
import uk.gov.gchq.maestro.commonutil.exception.MaestroRuntimeException;
import uk.gov.gchq.maestro.operation.Operation;

public final class FieldsUtil {

    public static final String FIELD_S_WAS_NOT_FOUND_IN_OPERATION_S = "field: %s was not found in operation: %s";
    public static final String FIELD_S_WAS_NOT_INSTANCE_OF_S = "field: %s was not instance of %s";

    private FieldsUtil() {
    }

    public static <E extends Enum<E>> void validate(final E e, final Operation operation, final Class instanceOf) {
        final Object obj = operation.get(e.name());
        if (obj == null) {
            throw new MaestroNullPointerException(String.format(FIELD_S_WAS_NOT_FOUND_IN_OPERATION_S, e.name(), operation.getId()));
        }
        if (!instanceOf.isAssignableFrom(obj.getClass())) {
            throw new MaestroRuntimeException(String.format(FIELD_S_WAS_NOT_INSTANCE_OF_S, e.name(), instanceOf));
        }
    }

    public static <E extends Enum<E>> Object get(final Operation operation, final E e) {
        return operation.get(e.name());
    }
}
