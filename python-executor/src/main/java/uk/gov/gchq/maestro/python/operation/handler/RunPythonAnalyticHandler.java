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

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ExecCreation;
import com.spotify.docker.client.messages.HostConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.python.operation.RunPythonAnalytic;
import uk.gov.gchq.maestro.python.operation.util.PythonUtils;

import java.io.File;

public class RunPythonAnalyticHandler implements OperationHandler<RunPythonAnalytic> {

    private String repositoryRootDirectory;
    private static final Logger LOGGER = LoggerFactory.getLogger(RunPythonAnalyticHandler.class);

    public RunPythonAnalyticHandler() {
        this(PythonUtils.DEFAULT_REPOSITORY_ROOT_DIRECTORY);
    }

    public RunPythonAnalyticHandler(final String repositoryRootDirectory) {
        this.repositoryRootDirectory = repositoryRootDirectory;
    }

    @Override
    public String doOperation(final RunPythonAnalytic operation, final Context context, final Executor executor) throws OperationException {
        return runPythonAnalytic(operation, context, executor);
    }

    private String runPythonAnalytic(final RunPythonAnalytic operation, final Context context, final Executor executor) throws OperationException {
        final DefaultDockerClient docker;

        // TODO get path from cache
        final File analyticDirectory = new File(repositoryRootDirectory, operation.getName());
        if (!analyticDirectory.exists()) {
            throw new OperationException("analytic does not exist");
        }

        try {
            docker = DefaultDockerClient.fromEnv().build();
        } catch (final DockerCertificateException e) {
            throw new OperationException(e);
        }

        String containerId = null;
        try {
//            docker.pull("python");
            final HostConfig hostConfig = HostConfig.builder()
                    .appendBinds(analyticDirectory.getAbsolutePath() + ":/analytic")
                    .build();
            final ContainerCreation container = docker.createContainer(ContainerConfig.builder()
                    .hostConfig(hostConfig)
                    .image("python")
                    .workingDir("/analytic")
                    .cmd("sh", "-c", "pip install -r requirements.txt")
                    .cmd("sh", "-c", "while :; do sleep 1; done")
                    .build());

            containerId = container.id();
            docker.startContainer(containerId);
            final ExecCreation execCreation;
            try {
//                final String[] runCommand = {"sh", "-c", "ls"};
                final String[] runCommand = {"python", "/analytic/main.py", new String(JSONSerialiser.serialise(operation.getInput()))};
                execCreation = docker.execCreate(containerId,
                        runCommand,
                        DockerClient.ExecCreateParam.attachStdout(),
                        DockerClient.ExecCreateParam.attachStderr());
            } catch (final SerialisationException e) {
                throw new OperationException("Failed to serilaise input", e);
            }

            final LogStream outputStream = docker.execStart(execCreation.id());
            return outputStream.readFully();

        } catch (final DockerException | InterruptedException e) {
            throw new OperationException(e);
        } finally {
            if (containerId != null) {
                try {
                    docker.killContainer(containerId);
                    docker.removeContainer(containerId);
                } catch (final Exception e) {
                    LOGGER.error("Failed to kill and remove container", e);
                }

            }
            docker.close();
        }
    }
}
