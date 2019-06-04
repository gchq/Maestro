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

package uk.gov.gchq.maestro.hook;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import uk.gov.gchq.maestro.commonutil.exception.CacheOperationException;
import uk.gov.gchq.maestro.named.operation.NamedOperationDetail;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.Operations;
import uk.gov.gchq.maestro.operation.handler.named.cache.NamedOperationCache;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A {@link Hook} to resolve named operations.
 */
@JsonPropertyOrder(alphabetic = true)
public class NamedOperationResolver implements Hook {
    private final NamedOperationCache cache;

    public NamedOperationResolver() {
        this(new NamedOperationCache());
    }

    public NamedOperationResolver(final NamedOperationCache cache) {
        this.cache = cache;
    }

    @Override
    public void preExecute(final Request request) {
        final Operation operation = request.getOperation();
        OperationChain opAsChain = OperationChain.wrap(operation.getId(), operation);
        resolveNamedOperations(opAsChain,
                request.getContext().getUser());
        request.setOperation(opAsChain);
    }

    private void resolveNamedOperations(final Operations operations, final User user) {
        final List<Operation> updatedOperations = new ArrayList<>(operations.getOperations().size());
        for (final Operation operation : operations.getOperations()) {
            if ("NamedOperation".equals(operation.getId())) {
                updatedOperations.addAll(resolveNamedOperation(operation, user));
            } else {
                if (operation instanceof Operations) {
                    resolveNamedOperations(((Operations) operation), user);
                }
                updatedOperations.add(operation);
            }
        }
        operations.updateOperations(updatedOperations);
    }

    private List<Operation> resolveNamedOperation(final Operation namedOp, final User user) {
        final NamedOperationDetail namedOpDetail;
        try {
            namedOpDetail = cache.getNamedOperation((String) namedOp.get("OperationName"), user);
        } catch (final CacheOperationException e) {
            // Unable to find named operation - just return the original named operation
            return Collections.singletonList(namedOp);
        }

        final OperationChain namedOperationChain = namedOpDetail.getOperationChain((Map<String, Object>) namedOp.get("Parameters"));
        updateOperationInput(namedOperationChain, namedOp.input());

        // Call resolveNamedOperations again to check there are no nested named operations
        resolveNamedOperations(namedOperationChain, user);
        return namedOperationChain.getOperations();
    }

    /**
     * Injects the input of the NamedOperation into the first operation in the OperationChain. This is used when
     * chaining NamedOperations together.
     *
     * @param opChain the resolved operation chain
     * @param input   the input of the NamedOperation
     */
    private void updateOperationInput(final OperationChain opChain,
                                      final Object input) {
        final Operation firstOp = opChain.getOperations().get(0);
        if (null != input && (firstOp.containsKey("Input") ) && null == (firstOp).input()) {
            ( firstOp).input(input);
        }
    }
}
