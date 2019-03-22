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

package uk.gov.gchq.maestro.operation.impl.job;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.exception.CloneFailedException;

import uk.gov.gchq.maestro.commonutil.CommonConstants;
import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.maestro.jobtracker.JobDetail;
import uk.gov.gchq.maestro.jobtracker.Repeat;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.io.Output;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;

import java.nio.charset.Charset;
import java.util.Map;

public class Job implements Output<JobDetail> {
    private static final String CHARSET_NAME = CommonConstants.UTF_8;
    private Repeat repeat;
    private Operation operation;

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

    @JsonSetter("operation")
    public void setOperation(final String operation) {
        try {
            this.operation = JSONSerialiser.deserialise(operation,
                    Operation.class);
        } catch (final SerialisationException e) {
            throw new IllegalArgumentException("Unable to deserialise Job OperationChain ", e);
        }
    }

    public void setOperation(final Operation operation) {
        this.operation = operation;
    }

    @Override
    public Operation shallowClone() throws CloneFailedException {
        return new Job.Builder()
                .operation(operation)
                .repeat(repeat)
                .build();
    }

    @Override
    public Map<String, String> getOptions() {
        return null;
    }

    @Override
    public Operation options(final Map<String, String> options) {
        return this;
    }

    @Override
    public TypeReference<JobDetail> getOutputTypeReference() {
        return new TypeReferenceImpl.JobDetail();
    }

    public static class Builder
            extends Operation.BaseBuilder<Job, Job.Builder> {
        public Builder() {
            super(new Job());
        }

        public Job.Builder operation(final Operation operation) {
            _getOp().setOperation(operation);
            return _self();
        }

        public Job.Builder operation(final String operation) {
            _getOp().setOperation(operation);
            return _self();
        }

        public Job.Builder repeat(final Repeat repeat) {
            _getOp().setRepeat(repeat);
            return _self();
        }
    }
}
