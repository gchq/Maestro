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
package uk.gov.gchq.maestro;

public class Executor {

    public <O> O execute(final Operation operation, final Context context) throws OperationException {
        return (O) handleOperation(operation, context);
    }

    private Object handleOperation(final Operation operation, final Context context) {
        Object result;
        OperationHandler<Operation> handler = getHandler();

        if (null != handler) {
            result = handler.doOperation(operation, context, this);
        } else if (operation instanceof DefaultOperation) {
            result = doUnhandledOperation(operation, context);
        } else {
            result = this.handleOperation(new DefaultOperation().setWrappedOp(operation), context);
        }

        return result;
    }

    private Object doUnhandledOperation(final Operation operation, final Context context) {
        return null;
    }

    private OperationHandler<Operation> getHandler() {
        return null;
    }


}
