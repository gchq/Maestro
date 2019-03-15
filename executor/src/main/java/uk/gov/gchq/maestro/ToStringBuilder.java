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

package uk.gov.gchq.maestro;

import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Extension of the Apache Commons {@link org.apache.commons.lang3.builder.ToStringBuilder}
 * class to provide a specific style for Maestro.
 */
public class ToStringBuilder extends org.apache.commons.lang3.builder.ToStringBuilder {
    public static final ToStringStyle SHORT_STYLE = new MaestroShortStyle();
    public static final ToStringStyle FULL_STYLE = new MaestroFullStyle();

    public ToStringBuilder(final Object object) {
        super(object, getMaestroToStringStyle());
    }

    private static ToStringStyle getMaestroToStringStyle() {
        if (DebugUtil.checkDebugMode()) {
            return FULL_STYLE;
        } else {
            return SHORT_STYLE;
        }
    }

    /**
     * Alternative {@link ToStringStyle} to give a more concise output.
     */
    public static class MaestroShortStyle extends MaestroFullStyle {
        private static final long serialVersionUID = 7974675454897453336L;

        public MaestroShortStyle() {
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
        }
    }

    /**
     * The default {@link ToStringStyle} to use in Maestro.
     */
    public static class MaestroFullStyle extends ToStringStyle {
        private static final long serialVersionUID = -6828867877202071837L;

        @Override
        public void append(final StringBuffer buffer, final String fieldName, final Object value, final Boolean fullDetail) {
            if (null != value) {
                super.append(buffer, fieldName, value, fullDetail);
            }
        }
    }
}
