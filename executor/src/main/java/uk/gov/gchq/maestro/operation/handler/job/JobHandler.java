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
import uk.gov.gchq.maestro.jobtracker.JobDetail;
import uk.gov.gchq.maestro.jobtracker.JobStatus;
import uk.gov.gchq.maestro.jobtracker.Repeat;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.declaration.FieldDeclaration;
import uk.gov.gchq.maestro.operation.handler.OutputOperationHandler;

import static uk.gov.gchq.maestro.operation.handler.job.util.JobExecutor.addOrUpdateJobDetail;
import static uk.gov.gchq.maestro.operation.handler.job.util.JobExecutor.executeJob;

public class JobHandler implements OutputOperationHandler<JobDetail> {
    @Override
    public JobDetail _doOperation(final Operation/*Job*/ operation, final Context context,
                                  final Executor executor) throws OperationException {
        JobDetail jobDetail = addOrUpdateJobDetail((Operation) operation.get("OpAsOperation"), context, null, JobStatus.RUNNING);
        jobDetail.setRepeat((Repeat) operation.get("Repeat"));

        return executeJob(jobDetail, context, executor);
    }

    @Override
    public FieldDeclaration getFieldDeclaration() {
        return new FieldDeclaration()
                .field("OpAsOperation", Operation.class)
                .field("Repeat", Repeat.class);
    }


}
