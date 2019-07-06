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
package uk.gov.gchq.maestro.operation.handler.chain;

import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.optimiser.OperationOptimiser;
import uk.gov.gchq.maestro.operation.validator.OperationValidation;
import uk.gov.gchq.maestro.operation.validator.OperationValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@code OperationChainHandler} handles {@link OperationChain}s.
 * To specify different OperationValidators or OperationOptimisers this
 * OperationChainHandler should be extended and new values for these fields
 * should be used.
 */
public class OperationChainHandler implements OperationHandler, OperationValidation {
    private OperationValidator opValidator =
            new OperationValidator();
    private List<OperationOptimiser> operationOptimisers = new ArrayList<>();

    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        return this.doOperation((OperationChain) operation, context, executor);
    }

    public Object doOperation(final OperationChain operationChain,
                              final Context context, final Executor executor) throws OperationException {
        Object result = null;
        for (final Operation op : operationChain.getOperations()) {
            updateOperationInput(op, result);
            result = executor.execute(op, context);
        }
        return result;
    }

    public OperationChain prepareOperation(final OperationChain operation,
                                           final Context context,
                                           final Executor executor) {
        final ValidationResult validationResult =
                opValidator.validate(operation, context.getUser(), executor);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Operation chain is invalid. " + validationResult
                    .getErrorString());
        }

        OperationChain optimisedOperation = operation;
        for (final OperationOptimiser operationOptimiser : operationOptimisers) {
            optimisedOperation = (OperationChain) operationOptimiser.optimise(optimisedOperation);
        }
        return optimisedOperation;
    }

    protected void updateOperationInput(final Operation op, final Object result) {
        if (null != result) {
            if (op instanceof OperationChain) {
                if (!((OperationChain) op).getOperations().isEmpty()) {
                    final Operation firstOp = ((OperationChain) op).getOperations()
                            .get(0);
                    if ("Input".equals(firstOp.getId())) {
                        setOperationInput(firstOp, result);
                    }
                }
            } else if ("Input".equals(op.getId())) {
                setOperationInput(op, result);
            }
        }
    }

    private void setOperationInput(final Operation op, final Object result) {
        if (null == op.input()) {
            op.operationArg("Input", result);
        }
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration(this.getClass());
    }
}
