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

package uk.gov.gchq.maestro.executor.operation.handler.job;

import org.junit.Ignore;
import org.junit.Test;

import uk.gov.gchq.maestro.operation.Operation;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class GetJobResultsHandlerTest {
    private static final String DEFAULT_KEY = "ALL";

    @Ignore(value = "TODO This logic needs to be migrated to Handler")
    @Test
    public void shouldThrowError() {
        // When
        try {
            final Operation jobResults = new Operation("GetJobResults")
                    .operationArg("key", "anythingElse");
            fail("Exception expected");
        } catch (IllegalArgumentException e) {
            assertEquals("Keys cannot be used with this operation", e.getMessage());
        }
    }

    @Ignore(value = "TODO This logic needs to be migrated to Handler")
    @Test
    public void shouldReturnNullIfSetKey() {
        // When
        final Operation jobResults = new Operation("GetJobResults")
                .operationArg("key", DEFAULT_KEY);

        // Then
        assertThat(jobResults.get("Key"), is(nullValue()));
    }

}
