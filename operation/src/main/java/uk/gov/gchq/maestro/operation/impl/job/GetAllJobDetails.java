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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.jobtracker.JobDetail;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.io.Output;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;

import java.util.Map;

/**
 * A {@code GetAllJobDetails} operation is used to retrieve all of the {@link JobDetail}s
 * related to a Gaffer graph.
 */
@JsonPropertyOrder(value = {"class"}, alphabetic = true)
@Since("1.0.0")
@Summary("Gets all running and historic job details")
public class GetAllJobDetails implements
        Output<CloseableIterable<JobDetail>> {
    private Map<String, String> options;

    @Override
    public TypeReference<CloseableIterable<JobDetail>> getOutputTypeReference() {
        return new TypeReferenceImpl.JobDetailIterable();
    }

    @Override
    public GetAllJobDetails shallowClone() {
        return new GetAllJobDetails.Builder()
                .options(options)
                .build();
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public void setOptions(final Map<String, String> options) {
        this.options = options;
    }

    public static class Builder extends Operation.BaseBuilder<GetAllJobDetails, Builder>
            implements Output.Builder<GetAllJobDetails, CloseableIterable<JobDetail>, Builder> {
        public Builder() {
            super(new GetAllJobDetails());
        }
    }
}
