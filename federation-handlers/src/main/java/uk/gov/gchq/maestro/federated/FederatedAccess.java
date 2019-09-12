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

package uk.gov.gchq.maestro.federated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.maestro.operation.user.User;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;


/**
 * Conditions required for a {@link uk.gov.gchq.maestro.operation.user.User} to have access to a graph within the
 * {@link uk.gov.gchq.maestro.executor.Executor} via {@link FederatedAccess}
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
@JsonPropertyOrder(value = {"class", "addingUserId", "auths"}, alphabetic = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class FederatedAccess implements Serializable, Comparable<FederatedAccess> {
    private static final long serialVersionUID = -8052273833606211340L;
    private boolean isPublic = Boolean.valueOf(FederatedStoreConstants.DEFAULT_VALUE_IS_PUBLIC);
    private Set<String> auths = new HashSet<>();
    private String addingUserId;
    private boolean disabledByDefault;

    public FederatedAccess(final Set<String> auths, final String addingUserId) {
        setAuths(auths);
        setAddingUserId(addingUserId);
    }

    public FederatedAccess(final Set<String> auths, final String addingUser, final boolean isPublic) {
        this(auths, addingUser);
        this.isPublic = isPublic;
    }

    @JsonCreator
    public FederatedAccess(@JsonProperty("auths") final Set<String> auths, @JsonProperty("addingUser") final String addingUser, @JsonProperty("isPublic") final boolean isPublic, @JsonProperty("disabledByDefault") final boolean disabledByDefault) {
        this(auths, addingUser, isPublic);
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
        return !Collections.disjoint(user.getOpAuths(), this.auths);
    }

    private boolean isAddingUser(final User user) {
        return null != user.getUserId() && user.getUserId().equals(addingUserId);
    }

    private boolean isAuthsNullOrEmpty() {
        return (null == this.auths || this.auths.isEmpty());
    }

    public FederatedAccess setAuths(final Set<String> auths) {
        if (nonNull(auths)) {
            this.auths = auths;
        } else {
            this.auths.clear();
        }
        return this;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public Set<String> getAuths() {
        return Collections.unmodifiableSet(auths);
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
                .append(auths, that.auths)
                .append(addingUserId, that.addingUserId)
                .append(disabledByDefault, that.disabledByDefault)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(isPublic)
                .append(auths)
                .append(addingUserId)
                .append(disabledByDefault)
                .toHashCode();
    }

    @Override
    public int compareTo(final FederatedAccess that) {
        requireNonNull(that, "tried to compare null object");
        final CompareToBuilder cb = new CompareToBuilder()
                .append(this.disabledByDefault, that.disabledByDefault)
                .append(this.isPublic, that.isPublic);

        if (0 == cb.append(this.addingUserId, that.addingUserId)
                .append(this.auths.size(), that.auths.size()).toComparison()) {

            final String[] thisAuths = this.auths.toArray(new String[0]);
            final String[] thatAuths = that.auths.toArray(new String[0]);

            for (int i = 0; i < thisAuths.length && 0 == cb.toComparison(); i++) {
                final String thisAuth = thisAuths[i];
                final String thatAuth = thatAuths[i];
                if (0 != cb.append(thisAuth, thatAuth).toComparison()) {
                    break;
                }
            }
        }

        return cb.toComparison();
    }
}
