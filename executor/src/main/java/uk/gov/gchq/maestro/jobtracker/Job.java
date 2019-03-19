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

package uk.gov.gchq.maestro.jobtracker;

import com.fasterxml.jackson.annotation.JsonIgnore;

import uk.gov.gchq.maestro.commonutil.CommonConstants;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.operation.Operation;

import java.nio.charset.Charset;

/**
 * POJO containing details of a scheduled Maestro job,
 * a {@link Repeat} and an Operation chain as a String.
 * To be used within the ExecuteJob for a ScheduledJob.
 */
public class Job {
    private static final String CHARSET_NAME = CommonConstants.UTF_8;
    private Repeat repeat;
    private Operation operation;

    public Job() {
    }

    public Job(final Repeat repeat) {
        this.repeat = repeat;
    }

    public Job(final Repeat repeat, final String opChain) {
        this.repeat = repeat;
        setOperation(opChain);
    }

    public Job(final Repeat repeat, final Operation operation) {
        this.repeat = repeat;
        this.operation = operation;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(final Repeat repeat) {
        this.repeat = repeat;
    }

    public String getOperation() {
        try {
            return new String(JSONSerialiser.serialise(operation),
                    Charset.forName(CHARSET_NAME));
        } catch (final SerialisationException se) {
            throw new IllegalArgumentException(se.getMessage());
        }
    }

    @JsonIgnore
    public Operation getOpAsOperation() {
        return operation;
    }

    public void setOperation(final String operation) {
        try {
            this.operation = JSONSerialiser.deserialise(operation,
                    Operation.class);
        } catch (final SerialisationException e) {
            throw new IllegalArgumentException("Unable to deserialise Job OperationChain ", e);
        }
    }
}
