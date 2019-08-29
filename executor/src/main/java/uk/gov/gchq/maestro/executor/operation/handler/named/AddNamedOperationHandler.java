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

package uk.gov.gchq.maestro.executor.operation.handler.named;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.executor.operation.handler.named.cache.NamedOperationCache;
import uk.gov.gchq.maestro.executor.util.ExecutorPropertiesUtil;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.named.NamedOperationDetail;
import uk.gov.gchq.maestro.operation.named.ParameterDetail;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Operation handler for AddNamedOperation which adds a Named Operation to the cache.
 */
@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class AddNamedOperationHandler implements OperationHandler {
    public static final String DESCRIPTION = "description";
    public static final String OPERATION_CHAIN = "operationChain";
    public static final String OPERATION_NAME = "operationName";
    public static final String PARAMETERS = "parameters";
    public static final String READ_ACCESS_ROLES = "readAccessRoles";
    public static final String SCORE = "score";
    public static final String WRITE_ACCESS_ROLES = "writeAccessRoles";
    public static final String OVERWRITE_FLAG = "overwriteFlag";
    private final NamedOperationCache cache;

    public AddNamedOperationHandler() {
        this(new NamedOperationCache());
    }

    public AddNamedOperationHandler(final NamedOperationCache cache) {
        this.cache = cache;
    }

    /**
     * Adds a NamedOperation to a cache which must be specified in the operation declarations file. An
     * NamedOperationDetail is built using the fields on the AddNamedOperation. The operation name and operation chain
     * fields must be set and cannot be left empty, or the build() method will fail and a runtime exception will be
     * thrown. The handler then adds/overwrites the NamedOperation according toa an overwrite flag.
     *
     * @param operation the {@link Operation} to be executed
     * @param context   the operation chain context, containing the user who executed the operation
     * @param executor  the {@link Executor} the operation should be run on
     * @return null (since the output is void)
     * @throws OperationException if the operation on the cache fails
     */
    @Override
    public Void _doOperation(final Operation operation,
                             final Context context, final Executor executor) throws OperationException {
        try {
            final NamedOperationDetail namedOperationDetail = new NamedOperationDetail.Builder()
                    .operationChain((OperationChain) operation.get(OPERATION_CHAIN))
                    .operationName((String) operation.get(OPERATION_NAME))
                    .creatorId(context.getUser().getUserId())
                    .readers((List<String>) operation.get(READ_ACCESS_ROLES))
                    .writers((List<String>) operation.get(WRITE_ACCESS_ROLES))
                    .description((String) operation.get(DESCRIPTION))
                    .parameters((Map<String, ParameterDetail>) operation.get(PARAMETERS))
                    .score((Integer) operation.get(SCORE))
                    .build();

            validate(namedOperationDetail.getOperationChainWithDefaultParams(), namedOperationDetail, executor.getOperationHandlerMap());

            cache.addNamedOperation(namedOperationDetail,
                    (Boolean) operation.getOrDefault(OVERWRITE_FLAG, false),
                    context.getUser(),
                    ExecutorPropertiesUtil.getAdminAuth(executor));
        } catch (final MaestroCheckedException e) {
            throw new OperationException(e.getMessage(), e);
        }
        return null;
    }

    private void validate(final OperationChain operationChain, final NamedOperationDetail namedOperationDetail, final Map<String, OperationHandler> operationHandlerMap) throws OperationException {
        final HashSet<String> operationKeysForThisHandler = new HashSet<>();

        for (final Map.Entry<String, OperationHandler> entry : operationHandlerMap.entrySet()) {
            final OperationHandler value = entry.getValue();
            if (this.equals(value)) {
                operationKeysForThisHandler.add(entry.getKey());
            }
        }

        for (final Operation operation : operationChain.getOperations()) {
            if (operationKeysForThisHandler.contains(operation.getId())) {
                throw new OperationException("Unapproved behaviour. OperationChain for AddNamedOperationHandler should not contain an operation handled by AddNamedOperationHandler. id: " + operation.getId());
            }
        }

        if (null != namedOperationDetail.getParameters()) { //TODO Review test this logic
            String operationString = namedOperationDetail.getOperations();
            for (final Map.Entry<String, ParameterDetail> parameterDetail : namedOperationDetail.getParameters().entrySet()) {
                String varName = "${" + parameterDetail.getKey() + "}";
                if (!operationString.contains(varName)) {
                    throw new OperationException("Parameter specified in NamedOperation doesn't occur in OperationChain string for " + varName);
                }
            }
        }
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration()
                .fieldRequired(OPERATION_NAME, String.class)
                .fieldRequired(DESCRIPTION, String.class)
                .fieldRequired(OPERATION_CHAIN, OperationChain.class)
                .fieldOptional(WRITE_ACCESS_ROLES, List.class) //TODO? check optional
                .fieldOptional(READ_ACCESS_ROLES, List.class) //TODO? check optional
                .fieldOptional(PARAMETERS, Map.class)
                .fieldOptional(SCORE, Integer.class)
                .fieldOptional(OVERWRITE_FLAG, Boolean.class);
    }

    @Override
    public boolean equals(final Object o) {
        return (this == o || (o != null && getClass() == o.getClass()));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .toHashCode();
    }
}
