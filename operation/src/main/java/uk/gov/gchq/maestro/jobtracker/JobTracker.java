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

import uk.gov.gchq.maestro.commonutil.cache.CacheServiceLoader;
import uk.gov.gchq.maestro.commonutil.exception.CacheOperationException;
import uk.gov.gchq.maestro.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.maestro.commonutil.iterable.WrappedCloseableIterable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@code JobTracker} is an entry in a Maestro cache service which is used to store
 * details of jobs submitted to the Executor.
 */
public final class JobTracker {

    private static final String CACHE_NAME = "JobTracker";

    private JobTracker() {
        // private constructor to prevent instantiation
    }

    /**
     * Add or update the job details relating to a job in the job tracker cache.
     *
     * @param jobDetail the job details to update
     */
    public static void addOrUpdateJob(final JobDetail jobDetail) {
        validateJobDetail(jobDetail);
        try {
            CacheServiceLoader.getService().putInCache(CACHE_NAME, jobDetail.getJobId(), jobDetail);
        } catch (final CacheOperationException e) {
            throw new RuntimeException("Failed to add jobDetail " + jobDetail.toString() + " to the cache", e);
        }
    }

    /**
     * Checks if the JobTracker cache is enabled
     *
     * @return true if the JobTracker cache is enabled
     */
    public static boolean isCacheEnabled() {
        return null != CacheServiceLoader.getService();
    }

    /**
     * Get the details of a specific job.
     *
     * @param jobId the ID of the job to lookup
     * @return the {@link JobDetail} object for the requested job
     */
    public static JobDetail getJob(final String jobId) {
        return CacheServiceLoader.getService().getFromCache(CACHE_NAME, jobId);
    }

    /**
     * Get all jobs from the job tracker cache.
     *
     * @return a {@link CloseableIterable} containing all of the job details
     */
    public static CloseableIterable<JobDetail> getAllJobs() {
        final Set<String> jobIds = CacheServiceLoader.getService().getAllKeysFromCache(CACHE_NAME);
        final List<JobDetail> jobs = jobIds.stream()
                .filter(Objects::nonNull)
                .map(jobId -> getJob(jobId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new WrappedCloseableIterable<>(jobs);
    }

    /**
     * Clear the job tracker cache.
     */
    public static void clear() {
        try {
            CacheServiceLoader.getService().clearCache(CACHE_NAME);
        } catch (final CacheOperationException e) {
            throw new RuntimeException("Failed to clear job tracker cache", e);
        }
    }

    private static void validateJobDetail(final JobDetail jobDetail) {
        if (null == jobDetail) {
            throw new IllegalArgumentException("JobDetail is required");
        }

        if (null == jobDetail.getJobId() || jobDetail.getJobId().isEmpty()) {
            throw new IllegalArgumentException("jobId is required");
        }
    }
}
