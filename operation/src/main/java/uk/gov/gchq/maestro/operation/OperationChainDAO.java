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

package uk.gov.gchq.maestro.operation;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;

import java.util.List;

/**
 * Simple data access object which enables the serialisation and deserialisation
 *
 * @param <OUT> the output type of the {@code OperationChainDAO}. This should
 *              match the output type of the last
 *              {@link uk.gov.gchq.maestro.operation.Operation}
 *              in the chain.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@Since("1.0.0")
@Summary("Simple data access object, enabling (de)serialisation of an OperationChain")
public class OperationChainDAO<OUT> extends OperationChain<OUT> {

    public OperationChainDAO() {
        super();
    }

    public OperationChainDAO(final Operation operation) {
        super(operation);
    }

    public OperationChainDAO(final List<Operation> operations) {
        super(operations);
    }

    public OperationChainDAO(final OperationChain<?> operationChain) {
        super(operationChain.getOperations());
        options(operationChain.getOptions());
    }


    /**
     * Get the class name of this class. This is set to always return {@code null}
     * in order to prevent the serialised version of this class from containing
     * the JSON type information that Jackson would use to deserialise JSON representations
     * of this class.
     *
     * @return null
     */
    @JsonGetter("class")
    public String getClassName() {
        return null;
    }

    @JsonSetter("class")
    public void setClassName(final String className) {
        if (null != className
                && !OperationChain.class.getName().equals(className)
                && !OperationChainDAO.class.getName().equals(className)
                && !OperationChain.class.getSimpleName().equals(className)
                && !OperationChainDAO.class.getSimpleName().equals(className)) {
            throw new IllegalArgumentException("Class name should be " + OperationChain.class.getName() + " or null");
        }
    }
}
