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
package uk.gov.gchq.maestro.helper;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class IterableUtil {
    private IterableUtil() {
        // Private constructor to prevent instantiation
    }

    public static void assertIterableEquals(final Iterable<?> expected, final Iterable<?> result) {
        assertIterableEquals(expected, result, false);
    }

    public static void assertIterableEquals(final Iterable<?> expected,
                                            final Iterable<?> result, final boolean ignoreDuplicates) {
        final List expectedCache = Lists.newArrayList(expected);
        final List resultCache = Lists.newArrayList(result);
        try {
            assertEquals(expectedCache, resultCache);
        } catch (final AssertionError err) {
            final List expectedList = Lists.newArrayList(expectedCache);
            final List resultList = Lists.newArrayList(resultCache);
            if (ignoreDuplicates) {
                expectedList.removeAll(resultCache);
                resultList.removeAll(expectedCache);
            } else {
                for (final Object obj : resultCache) {
                    expectedList.remove(obj);
                }
                for (final Object obj : expectedCache) {
                    resultList.remove(obj);
                }
            }

            /*final Comparator<ElementId> elementComparator = (element1, element2) -> {
                final String elementStr1 = null == element1 ? "" : element1.toString();
                final String elementStr2 = null == element2 ? "" : element2.toString();
                return elementStr1.compareTo(elementStr2);
            };
            expectedList.sort(elementComparator);
            resultList.sort(elementComparator);*/

            final List missingObjects = new ArrayList<>();
            for (final Object obj : expectedList) {
                missingObjects.add(obj);
            }

            final List incorrectObjects = new ArrayList<>();
            for (final Object obj : resultList) {
                incorrectObjects.add(obj);
            }

            assertTrue("\nMissing objects:\n(" + missingObjects.size() + ")" + missingObjects.toString()
                            + "\nUnexpected objects:\n(" + incorrectObjects.size() + ") " + incorrectObjects.toString(),
                    missingObjects.isEmpty() && incorrectObjects.isEmpty());
        }
    }
}
