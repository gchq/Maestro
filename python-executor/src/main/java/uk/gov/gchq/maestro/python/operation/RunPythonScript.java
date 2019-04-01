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
import uk.gov.gchq.maestro.commonutil.Required;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.io.InputOutput;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;

import java.util.Map;

public class RunPythonScript<I> implements InputOutput<I, ProcessOutput> {

    @Required
    private String scriptName;
    private Map<String, String> options;

    /**
     * TODO handle input
     */
    private I input;


    @Override
    public Operation shallowClone() throws CloneFailedException {
        return new RunPythonScript()
                .options(this.options)
                .scriptName(this.scriptName);
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }


    @Override
    public RunPythonScript options(final Map<String, String> options) {
        this.options = options;
        return this;
    }

    public String getScriptName() {
        return scriptName;
    }

    public RunPythonScript scriptName(final String scriptName) {
        this.scriptName = scriptName;
        return this;
    }

    @Override
    public ProcessOutput castToOutputType(final Object result) {
        return (ProcessOutput) result;
    }

    @Override
    public TypeReference<ProcessOutput> getOutputTypeReference() {
        return TypeReferenceImpl.createExplicitT();
    }

    @Override
    public I getInput() {
        return input;
    }

    @Override
    public void setInput(final I input) {
        this.input = input;
    }
}
