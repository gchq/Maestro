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

import org.eclipse.jgit.transport.CredentialsProvider;

import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclaration;
import uk.gov.gchq.maestro.operation.declaration.OperationDeclarations;
import uk.gov.gchq.maestro.python.operation.AddPythonAnalytic;
import uk.gov.gchq.maestro.python.operation.DeletePythonAnalytic;
import uk.gov.gchq.maestro.python.operation.RunPythonAnalytic;
import uk.gov.gchq.maestro.python.operation.UpdatePythonAnalytic;
import uk.gov.gchq.maestro.python.operation.handler.AddPythonAnalyticHandler;
import uk.gov.gchq.maestro.python.operation.handler.DeletePythonAnalyticHandler;
import uk.gov.gchq.maestro.python.operation.handler.RunPythonAnalyticHandler;
import uk.gov.gchq.maestro.python.operation.handler.UpdatePythonAnalyticHandler;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Config;
import uk.gov.gchq.maestro.util.Request;
import uk.gov.gchq.maestro.util.hook.Hook;

public class Sandbox {

    private static User USER = new User("User01");

    private static class ThrowExceptionHook implements Hook {
        @Override
        public <T> T onFailure(final T result, final Request request, final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) throws OperationException, SerialisationException {

        CredentialsProvider credentialsProvider = null;

        Config config = new Config.Builder()
                .addRequestHook(new ThrowExceptionHook())
                .operationHandlers(new OperationDeclarations.Builder()
                        .declaration(new OperationDeclaration.Builder()
                                .operation(AddPythonAnalytic.class)
                                .handler(new AddPythonAnalyticHandler()
                                        .credentialsProvider(credentialsProvider))
                                .build())
                        .declaration(new OperationDeclaration.Builder()
                                .operation(DeletePythonAnalytic.class)
                                .handler(new DeletePythonAnalyticHandler())
                                .build())
                        .declaration(new OperationDeclaration.Builder()
                                .operation(UpdatePythonAnalytic.class)
                                .handler(new UpdatePythonAnalyticHandler())
                                .build())
                        .declaration(new OperationDeclaration.Builder()
                                .operation(RunPythonAnalytic.class)
                                .handler(new RunPythonAnalyticHandler())
                                .build())
                        .build())
                .build();

        final Executor executor = new Executor()
                .config(config);

        executor.execute(new UpdatePythonAnalytic().name("blah"), USER);
    }
}
