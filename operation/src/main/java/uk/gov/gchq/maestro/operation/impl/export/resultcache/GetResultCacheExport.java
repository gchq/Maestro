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

package uk.gov.gchq.maestro.operation.impl.export.resultcache;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;

import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.export.Export;
import uk.gov.gchq.maestro.operation.export.GetExport;
import uk.gov.gchq.maestro.operation.io.Output;
import uk.gov.gchq.maestro.operation.serialisation.TypeReferenceImpl;

import java.util.Map;

/**
 * A {@code GetResultCacheExport} operation is used to retrieve data which
 * has previously been exported to a results cache.
 *
 * @see ExportToResultCache
 */
@JsonPropertyOrder(value = {"class"}, alphabetic = true)
@Since("1.0.0")
@Summary("Fetches data from a result cache")
public class GetResultCacheExport implements
        GetExport,
        Output<CloseableIterable<?>> {
    private String jobId;
    private String key = Export.DEFAULT_KEY;
    private Map<String, String> options;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(final String key) {
        this.key = key;
    }

    @Override
    public String getJobId() {
        return jobId;
    }

    @Override
    public void setJobId(final String jobId) {
        this.jobId = jobId;
    }

    @Override
    public TypeReference<CloseableIterable<?>> getOutputTypeReference() {
        return new TypeReferenceImpl.CloseableIterableObj();
    }

    @Override
    public GetResultCacheExport shallowClone() {
        return new GetResultCacheExport.Builder()
                .jobId(jobId)
                .key(key)
                .options(options)
                .build();
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public Operation options(final Map<String, String> options) {
        this.options = options;
        return this;
    }

    public static class Builder
            extends Operation.BaseBuilder<GetResultCacheExport, Builder>
            implements GetExport.Builder<GetResultCacheExport, Builder>,
            Output.Builder<GetResultCacheExport, CloseableIterable<?>, Builder> {
        public Builder() {
            super(new GetResultCacheExport());
        }
    }
}
