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

package uk.gov.gchq.maestro.operation.impl.export.set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Ignore;
import org.junit.Test;

import uk.gov.gchq.maestro.commonutil.iterable.ChainedIterable;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class SetExporterTest {

    @Ignore("Operation is POJO with no addition to arguments map.")
    @Test
    public void shouldAddIterablesToSet() {
        // Given
        final List<String> valuesA = Arrays.asList("1", "2", "3");
        final List<String> valuesB = Arrays.asList("4", "5", "6");
        final List<String> valuesCombined = Lists.newArrayList(new ChainedIterable<>(valuesA, valuesB));
        final Operation exporter = new Operation("SetExporter");

        // When
        // exporter.add("key", valuesA);
        // exporter.add("key", valuesB);

        // Then
        final CloseableIterable<?> export = (CloseableIterable<?>) exporter.get("key");
        assertEquals(Sets.newHashSet(valuesCombined), Sets.newHashSet(export));
    }

    @Test
    public void shouldAddIterablesToDifferentSets() {
        // Given
        final List<String> valuesA = Arrays.asList("1", "2", "3");
        final List<String> valuesB = Arrays.asList("4", "5", "6");
        final Operation exporter = new Operation("SetExporter");

        // When
        exporter.operationArg("key1", valuesA);
        exporter.operationArg("key2", valuesB);

        // Then
        final List key1 = (List) exporter.get("key1");
        final CloseableIterable<?> export1 = new ChainedIterable<>(key1);
        assertEquals(valuesA, Lists.newArrayList(export1));

        final List key2 = (List) exporter.get("key2");
        final CloseableIterable<?> export2 = new ChainedIterable<>(key2);
        assertEquals(valuesB, Lists.newArrayList(export2));
    }
}
