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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

import uk.gov.gchq.maestro.commonutil.CollectionUtil;
import uk.gov.gchq.maestro.commonutil.exception.UnauthorisedException;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.Operations;
import uk.gov.gchq.maestro.user.User;
import uk.gov.gchq.maestro.util.Request;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * An {@code OperationAuthoriser} is a {@link Hook} that checks a
 * user is authorised to execute an operation chain. This class requires a map
 * of operation authorisations.
 */
@JsonPropertyOrder(alphabetic = true)
public class OperationAuthoriser implements Hook {
    private final Set<String> allAuths = new HashSet<>();
    private final Map<Class<?>, Set<String>> auths = new HashMap<>();

    /**
     * Checks the {@link Operation} is allowed to be executed by the user.
     * This is done by checking the user's auths against the operation auths.
     * If an operation cannot be executed then an {@link IllegalAccessError} is thrown.
     *
     * @param request GraphRequest containing the Operation and Context
     */
    @Override
    public void preExecute(final Request request) {
        if (null != request.getOperation()) {
            authorise(request.getOperation(), request.getContext().getUser());
        }
    }

    /**
     * Add operation authorisations for a given operation class.
     * This can be called multiple times for the same operation class and the
     * authorisations will be appended.
     *
     * @param opClass the operation class
     * @param auths   the authorisations
     */
    public void addAuths(final Class<? extends Operation> opClass, final String... auths) {
        final Set<String> opAuths = this.auths.computeIfAbsent(opClass, k -> new HashSet<>());
        Collections.addAll(opAuths, auths);
        Collections.addAll(allAuths, auths);
    }

    @JsonIgnore
    public Map<Class<?>, Set<String>> getAuths() {
        return Collections.unmodifiableMap(auths);
    }

    @JsonIgnore
    public void setAuths(final Map<Class<?>, Set<String>> auths) {
        this.auths.clear();
        this.allAuths.clear();
        if (null != auths) {
            this.auths.putAll(auths);
            for (final Set<String> authsSet : auths.values()) {
                allAuths.addAll(authsSet);
            }
        }
    }

    @JsonGetter("auths")
    public Map<String, Set<String>> getAuthsAsStrings() {
        final Map<String, Set<String>> authsAsStrings = CollectionUtil.toMapWithStringKeys(auths);
        return Collections.unmodifiableMap(authsAsStrings);
    }

    @JsonSetter("auths")
    public void setAuthsFromStrings(final Map<String, Set<String>> auths) throws ClassNotFoundException {
        setAuths(CollectionUtil.toMapWithClassKeys(auths));
    }

    @JsonIgnore
    public Set<String> getAllAuths() {
        return Collections.unmodifiableSet(allAuths);
    }

    protected void authorise(final Operation operation, final User user) {
        if (null != operation) {
            if (operation instanceof Operations) {
                final Collection<? extends Operation> operations = ((Operations<?>) operation).getOperations();
                operations.forEach(op -> authorise(op, user));
            }

            final Class<? extends Operation> opClass = operation.getClass();
            final Set<String> userOpAuths = user.getOpAuths();
            boolean authorised = true;
            for (final Entry<Class<?>, Set<String>> entry : auths.entrySet()) {
                if ((entry.getKey().isAssignableFrom(opClass))
                        && (!userOpAuths.containsAll(entry.getValue()))) {
                    authorised = false;
                    break;
                }
            }

            if (!authorised) {
                throw new UnauthorisedException("User does not have permission to run operation: "
                        + operation.getClass().getName());
            }
        }
    }
}