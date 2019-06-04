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

package uk.gov.gchq.maestro.operation.handler.named;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.MaestroCheckedException;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.named.operation.NamedOperationDetail;
import uk.gov.gchq.maestro.named.operation.ParameterDetail;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.fields.FieldsUtil;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.handler.named.cache.NamedOperationCache;
import uk.gov.gchq.maestro.util.ExecutorPropertiesUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.Fields.Description;
import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.Fields.OperationChainAsString;
import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.Fields.OperationName;
import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.Fields.Parameters;
import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.Fields.ReadAccessRoles;
import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.Fields.Score;
import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.Fields.WriteAccessRoles;
import static uk.gov.gchq.maestro.operation.handler.named.AddNamedOperationHandler.Fields.overwriteFlag;

/**
 * Operation handler for AddNamedOperation which adds a Named Operation to the cache.
 */
public class AddNamedOperationHandler implements OperationHandler {
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
    public Void doOperation(final Operation operation,
                            final Context context, final Executor executor) throws OperationException {
        try {
            Arrays.stream(Fields.values()).forEach(f -> f.validate(operation));

            final NamedOperationDetail namedOperationDetail = new NamedOperationDetail.Builder()
                    .operationChain((String) OperationChainAsString.get(operation))
                    .operationName((String) OperationName.get(operation))
                    .creatorId(context.getUser().getUserId())
                    .readers((List<String>) ReadAccessRoles.get(operation))
                    .writers((List<String>) WriteAccessRoles.get(operation))
                    .description((String) Description.get(operation))
                    .parameters((Map<String, ParameterDetail>) Parameters.get(operation))
                    .score((Integer) Score.get(operation))
                    .build();

            validate(namedOperationDetail.getOperationChainWithDefaultParams(), namedOperationDetail);

            cache.addNamedOperation(namedOperationDetail, (Boolean) overwriteFlag.get(operation), context
                            .getUser(),
                    ExecutorPropertiesUtil.getAdminAuth(executor.getConfig().getProperties()));
        } catch (final MaestroCheckedException e) {
            throw new OperationException(e.getMessage(), e);
        }
        return null;
    }

    private void validate(final OperationChain operationChain, final NamedOperationDetail namedOperationDetail) throws OperationException {
        for (final Operation op : operationChain.getOperations()) {
            if ("NamedOperation".equals(op.getId())) {
                throw new OperationException("NamedOperations can not be nested within NamedOperations");
            }
        }

        if (null != namedOperationDetail.getParameters()) {
            String operationString = namedOperationDetail.getOperations();
            for (final Map.Entry<String, ParameterDetail> parameterDetail : namedOperationDetail.getParameters().entrySet()) {
                String varName = "${" + parameterDetail.getKey() + "}";
                if (!operationString.contains(varName)) {
                    throw new OperationException("Parameter specified in NamedOperation doesn't occur in OperationChain string for " + varName);
                }
            }
        }
    }

    public enum Fields {
        Description(String.class),
        OperationChainAsString(String.class),
        OperationName(String.class),
        Parameters(Map.class),
        ReadAccessRoles(List.class),
        Score(Integer.class),
        WriteAccessRoles(List.class),
        overwriteFlag(Boolean.class);

        Class instanceOf;

        Fields() {
            this(Object.class);
        }

        Fields(final Class instanceOf) {
            this.instanceOf = instanceOf;
        }

        public void validate(Operation operation) {
            FieldsUtil.validate(this, operation, instanceOf);
        }

        public Object get(Operation operation) {
            return FieldsUtil.get(operation, this);
        }
    }

}






