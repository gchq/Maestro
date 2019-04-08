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
package uk.gov.gchq.maestro.operation.auth;

import java.util.HashSet;
import java.util.Set;

public class OperationAuth {
    public enum OperationAuthOperator {
        AND, OR
    }

    private Class<?> opClass;
    private OperationAuthOperator operator = OperationAuthOperator.AND;
    private Set<String> auths;

    public OperationAuth() {
        this.auths = new HashSet<>();
    }

    public Class<?> getOpClass() {
        return opClass;
    }

    public OperationAuth opClass(final Class<?> opClass) {
        this.opClass = opClass;
        return this;
    }

    public OperationAuthOperator getOperator() {
        return operator;
    }

    public OperationAuth operator(final OperationAuthOperator operator) {
        this.operator = operator;
        return this;
    }

    public Set<String> getAuths() {
        return auths;
    }

    public OperationAuth auths(final Set<String> auths) {
        this.auths = auths;
        return this;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
