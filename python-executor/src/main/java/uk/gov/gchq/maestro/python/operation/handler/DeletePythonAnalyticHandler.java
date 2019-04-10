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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.python.operation.DeletePythonAnalytic;
import uk.gov.gchq.maestro.python.operation.util.PythonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DeletePythonAnalyticHandler implements OperationHandler<DeletePythonAnalytic> {

    private String repositoryRootDirectory;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeletePythonAnalyticHandler.class);

    public DeletePythonAnalyticHandler() {
        this(PythonUtils.DEFAULT_REPOSITORY_ROOT_DIRECTORY);
    }

    public DeletePythonAnalyticHandler(final String repositoryRootDirectory) {
        this.repositoryRootDirectory = repositoryRootDirectory;
    }

    @Override
    public Object doOperation(final DeletePythonAnalytic operation, final Context context, final Executor executor) throws OperationException {
        deletePythonAnalytic(operation, context, executor);
        return null;
    }

    public void deletePythonAnalytic(final DeletePythonAnalytic operation, final Context context, final Executor executor) throws OperationException {
        // TODO access stuff

        final File analyticDirectory = new File(repositoryRootDirectory, operation.getName());

        if (!analyticDirectory.exists()) {
            throw new OperationException("Analytic with name: " + operation.getName() + "does not exist");
        }

        try {
            recursivelyDelete(analyticDirectory);
        } catch (final IOException e) {
            LOGGER.error("Exception encountered when trying to remove directory: " + analyticDirectory, e);
            throw new OperationException("Failed to delete analytic: " + operation.getName());
        }
    }

    private void recursivelyDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            final File[] filesInDirectory = file.listFiles();
            if (filesInDirectory != null) {
                for (final File listedFile : filesInDirectory) {
                    recursivelyDelete(listedFile);
                }
            }
        }
        Files.delete(file.toPath());
    }

    public String getRepositoryRootDirectory() {
        return repositoryRootDirectory;
    }

    public DeletePythonAnalyticHandler repositoryRootDirectory(final String repositoryRootDirectory) {
        this.repositoryRootDirectory = repositoryRootDirectory;
        return this;
    }

}
