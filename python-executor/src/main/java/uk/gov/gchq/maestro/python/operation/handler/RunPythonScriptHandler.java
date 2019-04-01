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

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.cache.CacheServiceLoader;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.python.IScriptStorage;
import uk.gov.gchq.maestro.python.PythonUtils;
import uk.gov.gchq.maestro.python.operation.ProcessOutput;
import uk.gov.gchq.maestro.python.operation.RunPythonScript;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunPythonScriptHandler implements OperationHandler<RunPythonScript> {
    @Override
    public Object doOperation(final RunPythonScript operation, final Context context, final Executor executor) throws OperationException {
        return _doOperation(operation, context, executor);
    }

    private Object _doOperation(final RunPythonScript operation, final Context context, final Executor executor) throws OperationException {
        final IScriptStorage scriptStorage =
                CacheServiceLoader.getService().getFromCache(PythonUtils.PYTHON_CACHE_NAME, operation.getScriptName());

        if (scriptStorage == null) {
            throw new OperationException("The Script with name: \"" + operation.getScriptName() + "\" does not exist in the cache");
        }

        final File script = scriptStorage.getScript();

        Process pythonProcess;
        try {
            pythonProcess =
                    Runtime.getRuntime().exec("python " + script.getAbsolutePath());


        final BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));
        final BufferedReader processErrorReader =
                new BufferedReader(new InputStreamReader(pythonProcess.getErrorStream()));

        String line = null;

        StringBuilder outputBuilder = new StringBuilder();

        while ((line = processOutputReader.readLine()) != null){
            outputBuilder.append(line);
            outputBuilder.append('\n');
        }

        StringBuilder errorBuilder = new StringBuilder();
        while ((line = processErrorReader.readLine()) != null) {
            errorBuilder.append(line);
            errorBuilder.append('\n');
        }

        return new ProcessOutput()
                .setError(errorBuilder.toString())
                .setOutput(outputBuilder.toString());

        } catch (final IOException e) {
            throw new OperationException("Failed to run python", e);
        }


    }
}
