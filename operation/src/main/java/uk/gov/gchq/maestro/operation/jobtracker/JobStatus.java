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
package uk.gov.gchq.maestro.operation.jobtracker;

import uk.gov.gchq.koryphe.Summary;

/**
 * Denotes the status of a Maestro job.
 */
@Summary("The status of a job")
public enum JobStatus {

    /**
     * The Maestro job has been submitted and is running.
     */
    RUNNING,

    /**
     * The Maestro job has completed successfully.
     */
    FINISHED,

    /**
     * An error occured while executing the Maestro job.
     */
    FAILED,

    /**
     * The Maestro job is a parent job to a scheduled job(s).
     */
    SCHEDULED_PARENT,

    /**
     * The Maestro job is cancelled (to be used for scheduled jobs).
     */
    CANCELLED
}
