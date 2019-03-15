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

package uk.gov.gchq.maestro.operation.serialisation;

import com.fasterxml.jackson.core.type.TypeReference;

import uk.gov.gchq.koryphe.iterable.CloseableIterable;

/**
 * Utility class which contains a number of inner classes for different {@link TypeReference}s
 * used by the Gaffer project to denote the output type of an
 * {@link uk.gov.gchq.maestro.operation.io.Output}.
 *
 * @see uk.gov.gchq.maestro.operation.io.Output#getOutputTypeReference()
 */
public final class TypeReferenceImpl {
    private TypeReferenceImpl() {
    }

    public static class Void extends TypeReference<java.lang.Void> {
    }

    public static class String extends TypeReference<java.lang.String> {
    }

    public static class Long extends TypeReference<java.lang.Long> {
    }

    public static class Integer extends TypeReference<java.lang.Integer> {
    }

    public static class Object extends TypeReference<java.lang.Object> {
    }

    public static class Boolean extends TypeReference<java.lang.Boolean> {
    }

    public static class CloseableIterableObj extends
            TypeReference<CloseableIterable<?>> {
    }

    public static class IterableObj extends
            TypeReference<Iterable<?>> {
    }

    public static <T> TypeReference<T> createExplicitT() {
        return (TypeReference) new TypeReferenceImpl.Object();
    }

    public static <T> TypeReference<Iterable<? extends T>> createIterableT() {
        return (TypeReference) new IterableObj();
    }

    public static <T> TypeReference<CloseableIterable<? extends T>> createCloseableIterableT() {
        return (TypeReference) new CloseableIterableObj();
    }

    public static <T> TypeReference<Iterable<T>> createIterableExplicitT() {
        return (TypeReference) new IterableObj();
    }

    public static class Map extends TypeReference<java.util.LinkedHashMap> {
    }

    public static class MapStringObject extends TypeReference<java.util.Map<java.lang.String, java.lang.Object>> {
    }

    public static class MapStringSet extends TypeReference<java.util.Map<java.lang.String, java.util.Set<java.lang.Object>>> {
    }

    public static class Stream<T> extends TypeReference<java.util.stream.Stream<T>> {
    }

    public static class Array<T> extends TypeReference<T[]> {
    }

    public static class List<T> extends TypeReference<java.util.List<T>> {
    }

    public static class Set<T> extends TypeReference<java.util.Set<T>> {
    }

    public static class IterableMap extends TypeReference<Iterable<? extends java.util.Map<java.lang.String, java.lang.Object>>> {
    }

    public static class IterableString extends TypeReference<Iterable<? extends java.lang.String>> {
    }

    public static class IterableObject extends TypeReference<Iterable<? extends java.lang.Object>> {
    }

    public static class ListString extends TypeReference<java.util.List<java.lang.String>> {
    }

    public static class ValidationResult extends TypeReference<uk.gov.gchq.koryphe.ValidationResult> {

    }
}
