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

package uk.gov.gchq.maestro.data.generator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import java.util.function.Function;

/**
 * An {@code ObjectGenerator} converts objects into domain objects.
 * <p>
 * Implementations should be JSON serialisable.
 *
 * @param <INPUT>  the type of input object
 * @param <OUTPUT> the type of domain object
 */
@JsonTypeInfo(use = Id.CLASS, property = "class")
public interface ObjectGenerator<INPUT, OUTPUT> extends Function<Iterable<?
        extends INPUT>, Iterable<? extends OUTPUT>> {
}
