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

package uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation;

import com.fasterxml.jackson.databind.Module;

import java.util.List;

/**
 * A {@code JSONSerialiserModuleFactory} is a simple factory that returns
 * a list of {@link Module}s to be uses in an {@link com.fasterxml.jackson.databind.ObjectMapper}
 * in {@link JSONSerialiser}.
 */
public interface JSONSerialiserModules {
    List<Module> getModules();
}
