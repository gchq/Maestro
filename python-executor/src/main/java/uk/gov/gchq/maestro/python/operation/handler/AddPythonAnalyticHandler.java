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
package uk.gov.gchq.maestro.python.operation.handler;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.python.operation.AddPythonAnalytic;
import uk.gov.gchq.maestro.python.operation.util.PythonUtils;

import java.io.File;

public class AddPythonAnalyticHandler implements OperationHandler<AddPythonAnalytic> {

    private String repositoryRootDirectory;

    public AddPythonAnalyticHandler() {
        this(PythonUtils.DEFAULT_REPOSITORY_ROOT_DIRECTORY);
    }

    public AddPythonAnalyticHandler(final String repositoryRootDirectory) {
        this.repositoryRootDirectory = repositoryRootDirectory;
    }

    @Override
    public Object doOperation(final AddPythonAnalytic operation, final Context context, final Executor executor) throws OperationException {
        addPythonAnalytic(operation, context, executor);
        return null;
    }

    private void addPythonAnalytic(final AddPythonAnalytic operation, final Context context, final Executor exector) throws OperationException {

        final File file = new File(this.repositoryRootDirectory, operation.getName());

        if (file.exists()) {
            throw new OperationException("Python analytic with name: " + operation.getName() + " already exists");
        }

        try {
            Git.cloneRepository()
                    .setURI(operation.getRepositoryUrl())
                    .setDirectory(file)
                    .call();
        } catch (GitAPIException e) {
            throw new OperationException(e);
        }

        // TODO access stuff (maybe in callback)
    }

    public AddPythonAnalyticHandler repositoryRootDirectory(final String repositoryRootDirectory) {
        this.repositoryRootDirectory = repositoryRootDirectory;
        return this;
    }

    public String getRepositoryRootDirectory() {
        return this.repositoryRootDirectory;
    }
}
