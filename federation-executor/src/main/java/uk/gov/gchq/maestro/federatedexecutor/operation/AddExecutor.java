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

package uk.gov.gchq.maestro.federatedexecutor.operation;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.maestro.Executor;

import java.util.Set;

@JsonPropertyOrder(value = {"class, executor"}, alphabetic = true)
@JsonInclude(Include.NON_DEFAULT)
public class AddExecutor extends FederatedOperation<AddExecutor> {
    private Executor executor;
    private String parentConfigId;
    private Set<String> executorAuths;
    private boolean isPublic = false;
    private boolean disabledByDefault = FederatedExecutorStorage.DEFAULT_DISABLED_BY_DEFAULT;

    public AddExecutor() {
        addOption(FederatedStoreConstants.KEY_OPERATION_OPTIONS_EXECUTOR_IDS, ""); //TODO ?
    }

    @Override
    public AddExecutor shallowClone() throws CloneFailedException {
        final AddExecutor addExecutor = new AddExecutor()
                .executor(this.executor)
                .parentConfigId(this.parentConfigId)
                .disabledByDefault(this.disabledByDefault)
                .options(this.options)
                .publicFlag(this.isPublic);

        if (null != executorAuths) {
            addExecutor.auths(Sets.newHashSet(executorAuths.toArray(new String[executorAuths.size()])));
        }

        return addExecutor;
    }

    @JsonGetter("executor")
    public Executor getExecutor() {
        return executor;
    }

    @JsonSetter("executor")
    public AddExecutor executor(final Executor executor) {
        this.executor = executor;
        return this;
    }

    public String getParentConfigId() {
        return parentConfigId;
    }

    public AddExecutor parentConfigId(final String parentPropertiesId) {
        this.parentConfigId = parentPropertiesId;
        return this;
    }

    public boolean isDisabledByDefault() {
        return disabledByDefault;
    }

    public AddExecutor disabledByDefault(final boolean disabledByDefault) {
        this.disabledByDefault = disabledByDefault;
        return this;
    }

    @JsonGetter("executorAuths")
    public Set<String> getAuths() {
        return executorAuths;
    }

    @JsonSetter("executorAuths")
    public AddExecutor auths(final Set<String> executorAuths) {
        this.executorAuths = executorAuths;
        return this;
    }

    @JsonSetter("isPublic")
    public AddExecutor publicFlag(final boolean isPublic) {
        this.isPublic = isPublic;
        return this;
    }

    @JsonGetter("isPublic")
    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AddExecutor that = (AddExecutor) o;

        return new EqualsBuilder()
                .append(isPublic, that.isPublic)
                .append(disabledByDefault, that.disabledByDefault)
                .append(executor, that.executor)
                .append(parentConfigId, that.parentConfigId)
                .append(executorAuths, that.executorAuths)
                .append(options, that.options)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(executor)
                .append(parentConfigId)
                .append(executorAuths)
                .append(options)
                .append(isPublic)
                .append(disabledByDefault)
                .toHashCode();
    }
}
