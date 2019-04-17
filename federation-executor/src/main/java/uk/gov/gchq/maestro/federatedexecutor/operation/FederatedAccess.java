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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.maestro.user.User;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * Conditions required for a {@link User} to have access to a graph within the
 * {@link uk.gov.gchq.maestro.Executor} via {@link FederatedAccess}
 * <table summary="FederatedAccess truth table">
 * <tr><td> User Ops</td><td> AccessHook Ops</td><td> User added graph
 * </td><td> hasAccess?</td></tr>
 * <tr><td> 'A'     </td><td> 'A'           </td><td> n/a
 * </td><td> T         </td></tr>
 * <tr><td> 'A','B' </td><td> 'A'           </td><td> n/a
 * </td><td> T         </td></tr>
 * <tr><td> 'A'     </td><td> 'A','B'       </td><td> n/a
 * </td><td> T         </td></tr>
 * <tr><td> 'A'     </td><td> 'B'           </td><td> F
 * </td><td> F         </td></tr>
 * <tr><td> 'A'     </td><td> 'B'           </td><td> T
 * </td><td> T         </td></tr>
 * <tr><td> n/a     </td><td> {@code null}  </td><td> T
 * </td><td> T         </td></tr>
 * <tr><td> n/a     </td><td> {@code null}  </td><td> F
 * </td><td> F         </td></tr>
 * <tr><td> n/a     </td><td> {@code empty} </td><td> T
 * </td><td> T         </td></tr>
 * <tr><td> n/a     </td><td> {@code empty} </td><td> F
 * </td><td> F         </td></tr>
 * </table>
 *
 * @see #isValidToExecute(User)
 */
@JsonPropertyOrder(value = {"class", "addingUserId", "graphAuths"}, alphabetic = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class FederatedAccess implements Serializable {
    private static final long serialVersionUID = 1399629017857618033L;

    private boolean isPublic = Boolean.valueOf(FederatedStoreConstants.DEFAULT_VALUE_IS_PUBLIC);
    private Set<String> graphAuths = new HashSet<>();
    private String addingUserId;
    private boolean disabledByDefault;

    public FederatedAccess(final Set<String> graphAuths, final String addingUserId) {
        setGraphAuths(graphAuths);
        setAddingUserId(addingUserId);
    }

    public FederatedAccess(final Set<String> graphAuths, final String addingUser, final boolean isPublic) {
        this(graphAuths, addingUser);
        this.isPublic = isPublic;
    }

    @JsonCreator
    public FederatedAccess(@JsonProperty("graphAuths") final Set<String> graphAuths, @JsonProperty("addingUser") final String addingUser, @JsonProperty("isPublic") final boolean isPublic, @JsonProperty("disabledByDefault") final boolean disabledByDefault) {
        this(graphAuths, addingUser, isPublic);
        this.disabledByDefault = disabledByDefault;
    }

    public String getAddingUserId() {
        return addingUserId;
    }

    public void setAddingUserId(final String creatorUserId) {
        this.addingUserId = creatorUserId;
    }

    public boolean isDisabledByDefault() {
        return disabledByDefault;
    }

    /**
     * <table summary="isValidToExecute truth table">
     * <tr><td> hookAuthsEmpty  </td><td> isAddingUser</td><td>
     * userHasASharedAuth</td><td> isValid?</td></tr>
     * <tr><td>  T              </td><td> T           </td><td> n/a
     * </td><td> T   </td></tr>
     * <tr><td>  T              </td><td> F           </td><td> n/a
     * </td><td> F   </td></tr>
     * <tr><td>  F              </td><td> T           </td><td> n/a
     * </td><td> T   </td></tr>
     * <tr><td>  F              </td><td> n/a         </td><td> T
     * </td><td> T   </td></tr>
     * <tr><td>  F              </td><td> F           </td><td> F
     * </td><td> F   </td></tr>
     * </table>
     *
     * @param user User request permission.
     * @return boolean permission for user.
     */
    protected boolean isValidToExecute(final User user) {
        return isPublic || (null != user && (isAddingUser(user) || (!isAuthsNullOrEmpty() && isUserHasASharedAuth(user))));
    }

    private boolean isUserHasASharedAuth(final User user) {
        return !Collections.disjoint(user.getOpAuths(), this.graphAuths);
    }

    private boolean isAddingUser(final User user) {
        return null != user.getUserId() && user.getUserId().equals(addingUserId);
    }

    private boolean isAuthsNullOrEmpty() {
        return (null == this.graphAuths || this.graphAuths.isEmpty());
    }

    public FederatedAccess setGraphAuths(final Set<String> graphAuths) {
        if (Objects.nonNull(graphAuths)) {
            this.graphAuths = graphAuths;
        } else {
            this.graphAuths.clear();
        }
        return this;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public Set<String> getGraphAuths() {
        return Collections.unmodifiableSet(graphAuths);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FederatedAccess that = (FederatedAccess) o;

        return new EqualsBuilder()
                .append(isPublic, that.isPublic)
                .append(graphAuths, that.graphAuths)
                .append(addingUserId, that.addingUserId)
                .append(disabledByDefault, that.disabledByDefault)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(isPublic)
                .append(graphAuths)
                .append(addingUserId)
                .append(disabledByDefault)
                .toHashCode();
    }
}
