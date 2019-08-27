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

package uk.gov.gchq.maestro.rest;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.rules.TemporaryFolder;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.StreamUtil;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.rest.factory.DefaultExecutorFactory;
import uk.gov.gchq.maestro.util.Config;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;

public abstract class RestApiTestClient {
    protected final Client client = ClientBuilder.newClient();
    protected final ResourceConfig config;
    protected final String fullPath;
    protected final String root;
    protected final String path;
    protected final String versionString;
    protected final String uriString;
    protected HttpServer server;
    protected DefaultExecutorFactory defaultExecutorFactory;

    public RestApiTestClient(final String root, final String path, final String versionString, final ResourceConfig config) {
        this.root = root.replaceAll("/$", "");
        this.path = path.replaceAll("/$", "");
        this.versionString = versionString.replaceAll("/$", "");
        this.config = config;
        this.defaultExecutorFactory = new DefaultExecutorFactory();

        this.fullPath = this.path + '/' + versionString;
        this.uriString = this.root + '/' + this.fullPath;
    }

    public void stopServer() {
        if (null != server) {
            server.shutdownNow();
            server = null;
        }
    }

    public boolean isRunning() {
        return null != server;
    }

    public void reinitialiseExecutor(final TemporaryFolder testFolder) throws IOException {
        reinitialiseExecutor(testFolder, StreamUtil.EXECUTOR_CONFIG);
    }

    public void reinitialiseExecutor(final TemporaryFolder testFolder, final String configResourcePath) throws IOException {
        final Config configFromPath = Config.getConfigFromPath(RestApiTestClient.class, configResourcePath);
        reinitialiseExecutor(testFolder, configFromPath);
    }

    public void reinitialiseExecutor(final TemporaryFolder testFolder, final Config config) throws IOException {
        FileUtils.writeByteArrayToFile(testFolder.newFile(StreamUtil.EXECUTOR_CONFIG), config.serialise());

        // set properties for REST service
        System.setProperty(SystemProperty.EXECUTOR_CONFIG_PATH, testFolder.getRoot() + StreamUtil.EXECUTOR_CONFIG);

        reinitialiseExecutor();
    }

    public void reinitialiseExecutor(final Executor executor) throws IOException {
        DefaultExecutorFactory.setExecutor(executor);

        startServer();

        final SystemStatus status = getRestServiceStatus();

        if (SystemStatus.Status.UP != status.getStatus()) {
            throw new RuntimeException("The system status was not UP.");
        }
    }


    public void reinitialiseExecutor() throws IOException {
        defaultExecutorFactory.setExecutor(null);

        startServer();

        final SystemStatus status = getRestServiceStatus();

        if (SystemStatus.Status.UP != status.getStatus()) {
            throw new RuntimeException("The system status was not UP.");
        }
    }

    public abstract Response executeOperation(final Operation operation) throws IOException;

    public abstract Response executeOperationChain(final OperationChain opChain) throws IOException;

    public abstract Response executeOperationChainChunked(final OperationChain opChain) throws IOException;

    public abstract Response executeOperationChunked(final Operation operation) throws IOException;

    public abstract SystemStatus getRestServiceStatus();

    public abstract Response getOperationDetails(final Class clazz) throws IOException;

    public void startServer() throws IOException {
        if (null == server) {
            server = GrizzlyHttpServerFactory.createHttpServer(URI.create(uriString), config);
        }
    }

    public void restartServer() throws IOException {
        stopServer();
        startServer();
    }

    public String getPath() {
        return path;
    }

    public String getVersionString() {
        return versionString;
    }

    public String getRoot() {
        return root;
    }

    public DefaultExecutorFactory getDefaultExecutorFactory() {
        return defaultExecutorFactory;
    }

    public String getFullPath() {
        return fullPath;
    }

    public String getUriString() {
        return uriString;
    }
}
