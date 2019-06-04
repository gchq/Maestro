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
import uk.gov.gchq.maestro.jobtracker.JobStatus;
import uk.gov.gchq.maestro.jobtracker.JobTracker;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.fields.FieldsUtil;
import uk.gov.gchq.maestro.operation.handler.OperationHandler;
import uk.gov.gchq.maestro.operation.handler.output.ToStreamHandler;

import java.util.Arrays;

public class CancelScheduledJobHandler implements OperationHandler {
    @Override
    public Void doOperation(final Operation /*CancelScheduledJob*/ operation,
                            final Context context, final Executor executor) throws OperationException {
        Arrays.stream(ToStreamHandler.Fields.values()).forEach(f -> f.validate(operation));

        if (!JobTracker.isCacheEnabled()) {
            throw new OperationException("JobTracker not enabled");
        }
        //TODO Logic allows for null but Above validation will throw a uk.gov.gchq.maestro.commonutil.exception.MaestroRuntimeException
        final String o = (String) Fields.JobId.get(operation);
        if (null == o) {
            throw new OperationException("job id must be specified");
        }

        if (JobTracker.getJob(o).getStatus().equals(JobStatus.SCHEDULED_PARENT)) {
            JobTracker.getJob(o).setStatus(JobStatus.CANCELLED);
        } else {
            throw new OperationException("Job with jobId: " + o + " is not a scheduled job and cannot be cancelled.");
        }
        return null;
    }public enum Fields {
        JobId(String.class);

        Class instanceOf;

        Fields() {
            this(Object.class);
        }

        Fields(final Class instanceOf) {
            this.instanceOf = instanceOf;
        }

        public void validate(Operation operation) {
            FieldsUtil.validate(this, operation, instanceOf);
        }

        public Object get(Operation operation) {
            return FieldsUtil.get(operation, this);
        }
    }
}
