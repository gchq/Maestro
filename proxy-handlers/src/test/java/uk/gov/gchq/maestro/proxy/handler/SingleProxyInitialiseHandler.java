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

package uk.gov.gchq.maestro.proxy.handler;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.junit.rules.TemporaryFolder;

import uk.gov.gchq.maestro.commonutil.CommonTestConstants;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.rest.RestApiTestClient;
import uk.gov.gchq.maestro.rest.service.v2.RestApiV2TestClient;

import java.io.IOException;

@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class SingleProxyInitialiseHandler extends ProxyInitialiseHandler {
    public static final TemporaryFolder TEST_FOLDER = new TemporaryFolder(CommonTestConstants.TMP_DIRECTORY);
    private static final RestApiTestClient CLIENT = new RestApiV2TestClient();

    @Override
    public Object _doOperation(final Operation ignore, final Context context, final Executor executor) throws OperationException {
        startMapStoreRestApi();
        super._doOperation(ignore, context, executor);
        return null;
    }

    public void startMapStoreRestApi() throws OperationException { //TODO RENAME
        try {
            TEST_FOLDER.delete();
            TEST_FOLDER.create();
        } catch (final IOException e) {
            throw new OperationException("Unable to create temporary folder", e);
        }

        try {
            CLIENT.reinitialiseExecutor(TEST_FOLDER, "/remoteClientConfig.json");
        } catch (final IOException e) {
            throw new OperationException("Unable to reinitialise delegate executor", e);
        }
    }

}
