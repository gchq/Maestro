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

package uk.gov.gchq.maestro.operation.handler.output;

import com.google.common.collect.Lists;
import org.junit.Test;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.data.generator.StringGenerator;
import uk.gov.gchq.maestro.operation.impl.output.ToCsv;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class
ToCsvHandlerTest {

    @Test
    public void shouldConvertToCsv() throws OperationException {
        // Given
        final List<Integer> inputObjs = Lists.newArrayList(
                2,
                4,
                5,
                3,
                6
        );

        final ToCsv operation = new ToCsv.Builder<>()
                .input(inputObjs)
                .generator(new StringGeneratorWithConcatImpl())
                .build();

        final ToCsvHandler handler = new ToCsvHandler();

        //When
        final Iterable<? extends String> results = handler.doOperation(operation, new Context(), null);

        //Then
        final List<String> resultList = Lists.newArrayList(results);
        assertEquals(Arrays.asList(
                "2concatString",
                "4concatString",
                "5concatString",
                "3concatString",
                "6concatString"
        ), resultList);
    }

    private class StringGeneratorWithConcatImpl extends StringGenerator<Integer> {

        @Override
        protected String _apply(final Integer object) {
            return object.toString() + "concatString";
        }
    }
}
