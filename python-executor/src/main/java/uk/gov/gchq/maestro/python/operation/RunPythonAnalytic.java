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
package uk.gov.gchq.maestro.python.operation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.maestro.operation.io.InputOutput;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;

import java.util.Map;

public class RunPythonAnalytic<I> implements InputOutput<I, String> {

    private I input;
    private Map<String, String> options;
    private String name;

    @Override
    public I getInput() {
        return input;
    }

    @Override
    public void setInput(final I input) {
        this.input = input;
    }

    public RunPythonAnalytic input(final I input) {
        this.input = input;
        return this;
    }

    @Override
    public TypeReference<String> getOutputTypeReference() {
        return new TypeReferenceImpl.String();
    }

    @Override
    public RunPythonAnalytic shallowClone() throws CloneFailedException {
        return new RunPythonAnalytic<I>()
                .input(input)
                .name(name)
                .options(options);
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public RunPythonAnalytic options(final Map<String, String> options) {
        this.options = options;
        return this;
    }

    public String getName() {
        return name;
    }

    public RunPythonAnalytic name(final String name) {
        this.name = name;
        return this;
    }
}
