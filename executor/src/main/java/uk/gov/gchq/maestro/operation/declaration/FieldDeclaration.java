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
package uk.gov.gchq.maestro.operation.declaration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import uk.gov.gchq.maestro.commonutil.ToStringBuilder;

import java.util.TreeMap;
import java.util.TreeSet;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
@JsonPropertyOrder(value = {"class", "handlerClass"}, alphabetic = true)
public class FieldDeclaration {
    private TreeMap<String, Class> fieldDeclarations = new TreeMap<>(String::compareToIgnoreCase);
    private TreeSet<String> optionalFields = new TreeSet<>(String::compareToIgnoreCase);

    @JsonCreator
    public FieldDeclaration(@JsonProperty("fieldDeclarations") final TreeMap<String, Class> fieldDeclarations) {
        this.fieldDeclarations = fieldDeclarations;
    }

    public FieldDeclaration() {
    }

    public TreeMap<String, Class> getFieldDeclarations() {
        return fieldDeclarations;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fieldDeclarations", fieldDeclarations)
                .build();
    }

    public FieldDeclaration field(final String field, final Class valueClass) {
        fieldDeclarations.put(field, valueClass);
        return this;
    }

    public FieldDeclaration fieldOptional(final String field, final Class valueClass) {
        field(field, valueClass);
        optionalFields.add(field);
        return this;
    }

    public FieldDeclaration fieldRequired(final String field, final Class valueClass) {
        field(field, valueClass);
        optionalFields.remove(field);
        return this;
    }

    public boolean optionalContains(final String field) {
        return optionalFields.contains(field);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final FieldDeclaration that = (FieldDeclaration) o;

        return new EqualsBuilder()
                .append(fieldDeclarations, that.fieldDeclarations)
                .append(optionalFields, that.optionalFields)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fieldDeclarations)
                .append(optionalFields)
                .toHashCode();
    }
}
