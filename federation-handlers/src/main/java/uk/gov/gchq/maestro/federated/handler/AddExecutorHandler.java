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

package uk.gov.gchq.maestro.federated.handler;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.executor.Context;
import uk.gov.gchq.maestro.executor.Executor;
import uk.gov.gchq.maestro.executor.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.executor.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.federated.util.AddExecutorsFederatedUtil;
import uk.gov.gchq.maestro.operation.Operation;

import java.util.Set;

@JsonPropertyOrder(value = {"class"}, alphabetic = true)
public class AddExecutorHandler implements OperationHandler {

    public static final String ERROR_ADDING_EXECUTOR_ID_S_TO_S_S = "Error adding executorId: %s to: %s. -> %s";
    public static final String EXECUTOR = "executor";
    public static final String IS_PUBLIC = "isPublic";
    public static final String DISABLED_BY_DEFAULT = "disabledByDefault";
    public static final String AUTHS = "auths";
    public static final String CUSTOM_CONFIG_AUTHS = "customConfigAuths";
    public static final String PARENT_CONFIG_ID = "parentConfigId";

    @Override
    public Object _doOperation(final Operation operation, final Context context, final Executor executor) throws OperationException {
        try {
            AddExecutorsFederatedUtil.addExecutorTo(executor, operation, context);
        } catch (final Exception e) {
            throw new OperationException(String.format(ERROR_ADDING_EXECUTOR_ID_S_TO_S_S, ((Executor) operation.get(EXECUTOR)).getId(), executor.getId(), e.getMessage()), e);
        }

        return null;
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration().fieldRequired(EXECUTOR, Executor.class)
                .fieldOptional(IS_PUBLIC, Boolean.class)
                .fieldOptional(DISABLED_BY_DEFAULT, Boolean.class)
                .fieldOptional(AUTHS, Set.class)
                .fieldOptional(PARENT_CONFIG_ID, String.class);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .toHashCode();
    }
}
