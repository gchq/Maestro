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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.google.common.collect.Sets;

import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.federatedexecutor.operation.FederatedAccess;
import uk.gov.gchq.maestro.federatedexecutor.operation.FederatedExecutorStorage;

import java.io.IOException;
import java.util.HashSet;

public class MapStorageDeserialiser extends KeyDeserializer {
    @Override
    public FederatedAccess deserializeKey(final String s, final DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
       return JSONSerialiser.deserialise(s,FederatedAccess.class);
    }
}
