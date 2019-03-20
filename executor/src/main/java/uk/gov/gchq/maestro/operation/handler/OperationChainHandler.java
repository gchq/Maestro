/*
 * Copyright 2017-2018 Crown Copyright
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
package uk.gov.gchq.maestro.operation.handler;

import uk.gov.gchq.koryphe.ValidationResult;
import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.OperationValidation;
import uk.gov.gchq.maestro.operation.OperationValidator;
import uk.gov.gchq.maestro.operation.OutputOperationHandler;
import uk.gov.gchq.maestro.operation.io.Input;
import uk.gov.gchq.maestro.optimiser.OperationOptimiser;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@code OperationChainHandler} handles {@link OperationChain}s.
 * To specify different OperationValidators or OperationOptimisers this
 * OperationChainHandler should be extended and new values for these fields
 * should be used.
 *
 * @param <OUT> the output type of the operation chain
 */
public class OperationChainHandler<OUT> implements OutputOperationHandler<OperationChain<OUT>, OUT>, OperationValidation<OperationChain<OUT>> {
    private OperationValidator opValidator =
            new OperationValidator();
    private List<OperationOptimiser> operationOptimisers = new ArrayList<>();

    @Override
    public OUT doOperation(final OperationChain<OUT> operationChain,
                           final Context context, final Executor executor) {
        Object result = null;
        for (final Operation op : operationChain.getOperations()) {
            updateOperationInput(op, result);
            result = executor.execute(op, context);
        }
        return (OUT) result;
    }

    @Override
    public OperationChain<OUT> prepareOperation(final OperationChain<OUT> operation,
                                                final Context context,
                                                final Executor executor) {
        final ValidationResult validationResult =
                opValidator.validate(operation, context.getUser(), executor);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Operation chain is invalid. " + validationResult
                    .getErrorString());
        }

        OperationChain<OUT> optimisedOperation = operation;
        for (final OperationOptimiser operationOptimiser : operationOptimisers) {
            optimisedOperation = (OperationChain<OUT>) operationOptimiser.optimise(optimisedOperation);
        }
        return optimisedOperation;
    }

    protected void updateOperationInput(final Operation op, final Object result) {
        if (null != result) {
            if (op instanceof OperationChain) {
                if (!((OperationChain) op).getOperations().isEmpty()) {
                    final Operation firstOp = (Operation) ((OperationChain) op).getOperations()
                            .get(0);
                    if (firstOp instanceof Input) {
                        setOperationInput(firstOp, result);
                    }
                }
            } else if (op instanceof Input) {
                setOperationInput(op, result);
            }
        }
    }

    private void setOperationInput(final Operation op, final Object result) {
        if (null == ((Input) op).getInput()) {
            ((Input) op).setInput(result);
        }
    }
}
