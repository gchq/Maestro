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
import uk.gov.gchq.maestro.commonutil.ExecutorService;
import uk.gov.gchq.maestro.commonutil.exception.OperationException;
import uk.gov.gchq.maestro.jobtracker.JobDetail;
import uk.gov.gchq.maestro.jobtracker.JobStatus;
import uk.gov.gchq.maestro.jobtracker.JobTracker;
import uk.gov.gchq.maestro.operation.Operation;
import uk.gov.gchq.maestro.operation.OperationChain;
import uk.gov.gchq.maestro.operation.Operations;
import uk.gov.gchq.maestro.operation.OutputOperationHandler;
import uk.gov.gchq.maestro.operation.impl.export.resultcache.ExportToResultCache;
import uk.gov.gchq.maestro.operation.impl.job.Job;

public class JobHandler implements OutputOperationHandler<Job, JobDetail> {
    @Override
    public JobDetail doOperation(final Job operation, final Context context,
                                 final Executor executor) throws OperationException {
        JobDetail jobDetail = executor.addOrUpdateJobDetail(operation.getOpAsOperation(),
                context, null, JobStatus.RUNNING);

        jobDetail.setRepeat(operation.getRepeat());

        return executeJob(jobDetail, context, executor);
    }

    private JobDetail executeJob(final Operation operation,
                                 final Context context,
                                 final String parentJobId,
                                 final Executor executor) throws OperationException {
        JobDetail childJobDetail = executor.addOrUpdateJobDetail(operation, context, null, JobStatus.RUNNING);
        childJobDetail.setParentJobId(parentJobId);
        return executeJob(childJobDetail, context, executor);
    }

    private JobDetail executeJob(final JobDetail jobDetail,
                                 final Context context, final Executor executor) throws OperationException {
        if (!JobTracker.isJobTrackerCacheEnabled()) {
            throw new OperationException("JobTracker has not been configured.");
        }

        if (null == ExecutorService.getService() || !ExecutorService.isEnabled()) {
            throw new OperationException(("Executor Service is not enabled."));
        }

        if (null != jobDetail.getRepeat()) {
            return scheduleJob(jobDetail, context, executor);
        } else {
            return runJob(jobDetail, context, executor);
        }
    }

    private JobDetail scheduleJob(final JobDetail parentJobDetail,
                                  final Context context, final Executor executor) {
        executor.getExecutorService().scheduleAtFixedRate(() -> {
            if ((JobTracker.getJob(parentJobDetail.getJobId(),
                    context.getUser()).getStatus().equals(JobStatus.CANCELLED))) {
                Thread.currentThread().interrupt();
                return;
            }
            final Operation operation =
                    parentJobDetail.getOpAsOperation().shallowClone();
            final Context newContext = context.shallowClone();
            try {
                executeJob(operation, newContext, parentJobDetail.getJobId(),
                        executor);
            } catch (final OperationException e) {
                throw new RuntimeException("Exception within scheduled job", e);
            }
        }, parentJobDetail.getRepeat().getInitialDelay(), parentJobDetail.getRepeat().getRepeatPeriod(), parentJobDetail.getRepeat().getTimeUnit());

        return executor.addOrUpdateJobDetail(parentJobDetail.getOpAsOperation(), context, null, JobStatus.SCHEDULED_PARENT);
    }

    private JobDetail runJob(final JobDetail jobDetail, final Context context, final Executor executor) {
        Operation operation = jobDetail.getOpAsOperation();
        final OperationChain<?> opChain;

        if (operation instanceof Operations) {
            opChain = (OperationChain<?>) operation;
        } else {
            opChain = OperationChain.wrap(operation);
        }

        if (executor.isSupported(ExportToResultCache.class)) {
            boolean hasExport = false;

            for (final Operation op : opChain.getOperations()) {
                if (op instanceof ExportToResultCache) {
                    hasExport = true;
                    break;
                }
            }
            if (!hasExport) {
                opChain.getOperations()
                        .add(new ExportToResultCache<>());
            }
        }

        executor.runAsync(() -> {
            try {
                executor.execute(opChain, context);
                executor.addOrUpdateJobDetail(opChain, context, null,
                        JobStatus.FINISHED);
            } catch (final Error e) {
                executor.addOrUpdateJobDetail(opChain, context, e.getMessage(),
                        JobStatus.FAILED);
                throw e;
            } catch (final Exception e) {
                executor.addOrUpdateJobDetail(opChain, context, e.getMessage(),
                        JobStatus.FAILED);
            }
        });
        return jobDetail;
    }
}
