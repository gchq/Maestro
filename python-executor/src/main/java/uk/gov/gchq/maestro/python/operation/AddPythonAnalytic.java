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

import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.maestro.operation.Operation;

import java.util.HashMap;
import java.util.Map;

public class AddPythonAnalytic implements Operation {

    private Map<String, String> options = new HashMap<>();
    private String name;
    private String repositoryUrl;


    @Override
    public Operation shallowClone() throws CloneFailedException {
        return new AddPythonAnalytic()
                .options(this.options)
                .repositoryUrl(this.repositoryUrl)
                .name(this.name);
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public AddPythonAnalytic options(final Map<String, String> options) {
        this.options = options;
        return this;
    }

    public String getName() {
        return name;
    }

    public AddPythonAnalytic name(final String name) {
        this.name = name;
        return this;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public AddPythonAnalytic repositoryUrl(final String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
        return this;
    }
}