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

package uk.gov.gchq.maestro.commonutil.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @see java.util.Objects#requireNonNull(Object, String)
 * @see java.util.Objects#requireNonNull(Object, Supplier)
 */
public final class MaestroObjectsUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaestroObjectsUtil.class);

    public static final String S_S_S_IS_NULL = "%s%s%s is null";
    public static final String DUE_TO = " due to -> ";
    public static final String EMPTY = "";

    private MaestroObjectsUtil() {
        //empty
    }

    public static <T> T requireNonNull(final T obj, final String name) throws MaestroNullPointerException {
        return requireNonNull(obj, name, "");
    }

    public static <T> T requireNonNull(final T obj, final String name, final String prefix) throws MaestroNullPointerException {
        if (obj == null) {
            final String message = String.format(S_S_S_IS_NULL, prefix, prefix.isEmpty() ? EMPTY : DUE_TO, name);
            LOGGER.error(message);
            throw new MaestroNullPointerException(message);
        }
        return obj;
    }

    public static <T> T requireNonNull(final T obj, final String name, final Supplier<String> prefixSupplier) throws MaestroNullPointerException {
        if (obj == null) {
            final String prefix = prefixSupplier.get();
            final String message = String.format(S_S_S_IS_NULL, prefix, prefix.isEmpty() ? EMPTY : DUE_TO, name);
            LOGGER.error(message);
            throw new MaestroNullPointerException(message);
        }
        return obj;
    }

}
