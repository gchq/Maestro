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

package uk.gov.gchq.maestro.commonutil.serialisation.impl.util;

import java.io.Serializable;

public class ParameterisedTestObject<K> implements Serializable {

    private String x = "TEST";
    private K k = null;

    public String getX() {
        return x;
    }

    public void setX(final String x) {
        this.x = x;
    }

    public K getK() {
        return k;
    }

    public void setK(final K k) {
        this.k = k;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ParameterisedTestObject) {
            ParameterisedTestObject that = (ParameterisedTestObject) obj;
            return super.equals(obj) || (this.getX().equals(that.getX()) && this.getK().equals(that.getK()));
        } else {
            return false;
        }
    }
}
