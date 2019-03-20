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

package uk.gov.gchq.maestro.operation.handler.job;

import uk.gov.gchq.maestro.Context;
import uk.gov.gchq.maestro.Executor;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.jobtracker.JobDetail;
import uk.gov.gchq.maestro.jobtracker.JobTracker;
import uk.gov.gchq.maestro.operation.OutputOperationHandler;
import uk.gov.gchq.maestro.operation.impl.job.GetAllJobDetails;

import static uk.gov.gchq.maestro.commonutil.exception.Status.SERVICE_UNAVAILABLE;

/**
 * A {@code GetAllJobDetailsHandler} handles {@link GetAllJobDetails} operations
 * by querying the configured store's job tracker for all job information.
 */
public class GetAllJobDetailsHandler implements OutputOperationHandler<GetAllJobDetails, CloseableIterable<JobDetail>> {
    @Override
    public CloseableIterable<JobDetail> doOperation(final GetAllJobDetails operation, final Context context, final Executor executor) throws OperationException {
        if (!JobTracker.isJobTrackerCacheEnabled()) {
            throw new OperationException("The Job Tracker has not been configured", SERVICE_UNAVAILABLE);
        }
        return JobTracker.getAllJobs(context.getUser());
    }
}
