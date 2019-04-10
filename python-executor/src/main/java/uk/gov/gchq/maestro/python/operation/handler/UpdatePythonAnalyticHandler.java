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

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.python.operation.util.PythonUtils;
import uk.gov.gchq.maestro.python.operation.UpdatePythonAnalytic;

import java.io.File;
import java.io.IOException;

public class UpdatePythonAnalyticHandler implements OperationHandler<UpdatePythonAnalytic> {

    private String repositoryRootDirectory;

    public UpdatePythonAnalyticHandler() {
        this(PythonUtils.DEFAULT_REPOSITORY_ROOT_DIRECTORY);
    }

    public UpdatePythonAnalyticHandler(final String repositoryRootDirectory) {
        this.repositoryRootDirectory = repositoryRootDirectory;
    }

    @Override
    public Object doOperation(final UpdatePythonAnalytic operation, final Context context, final Executor executor) throws OperationException {
        updatePythonAnalytic(operation, context, executor);
        return null;
    }

    private void updatePythonAnalytic(final UpdatePythonAnalytic operation, final Context context, final Executor executor) throws OperationException {
        File analytic = new File(repositoryRootDirectory, operation.getName());

        if (!analytic.exists()) {
            throw new OperationException("Python analytic with name: " + operation.getName() + " does not exist");
        }

        try {
            Git.open(analytic).pull();
        } catch (final IOException e) {
            throw new OperationException("Failed to update git repository", e);
        }
    }

    public UpdatePythonAnalyticHandler repositoryRootDirectory(final String repositoryRootDirectory) {
        this.repositoryRootDirectory = repositoryRootDirectory;
        return this;
    }

    public String getRepositoryRootDirectory() {
        return this.repositoryRootDirectory;
    }
}
