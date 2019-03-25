package cz.fit.metacentrum.service.action.status.ex

import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.action.status.QueueRecordsService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.JobUtils
import javax.inject.Inject


/**
 * Executor that checks queue for jobs.
 * @author Jakub Tucek
 */
class CheckQueueExecutor : TaskExecutor {

    @Inject
    private lateinit var queueRecordsService: QueueRecordsService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        if (metadata.currentState.isFinishing()) {
            return metadata
        }
        val jobs = metadata.jobs ?: throw IllegalStateException("Jobs are missing from metadata object")

        // mapped job by pid so queue records can be mapped to jobs easily
        val mappedJobsByPid = jobs
                .map { (it.jobInfo.pid ?: throw IllegalStateException("Missing pid on task ${it}")) to it }
                .toMap()
        // read queue job records mapped by its state
        val mappedByPid = retrieveQueuedJobs(metadata, mappedJobsByPid)
        val updatedJobs = jobs.map { updateJobState(it, mappedByPid) }

        return metadata.copy(jobs = updatedJobs)
    }

    private fun retrieveQueuedJobs(metadata: ExecutionMetadata, mapedJobsByPid: Map<String, ExecutionMetadataJob>): Map<String, List<QueueRecord>> {
        val username = metadata.submittingUsername ?: throw IllegalStateException("Submitted username is missing")
        val queueList = queueRecordsService.retrieveQueueForUser(username)

        return queueList.filter { mapedJobsByPid.containsKey(it.pid) }
                .groupBy { it.pid }
    }

    private fun updateJobState(job: ExecutionMetadataJob, pidMap: Map<String, List<QueueRecord>>): ExecutionMetadataJob {
        val record = pidMap[job.jobInfo.pid]?.first()

        if (record != null) {
            return when (record.state) {
                QueueRecord.State.RUNNING -> job.copy(
                        jobInfo = job.jobInfo.copy(
                                state = ExecutionMetadataState.RUNNING,
                                runningTime = record.elapsedTime
                        )
                )
                else -> job
            }
        }
        // has no record but state was/still is considered as RUNNING/QUEUED by previous executors
        // this means that job was probably killed as it did not finish and is not present in QUEUE
        if (job.jobInfo.state == ExecutionMetadataState.RUNNING || job.jobInfo.state == ExecutionMetadataState.QUEUED) {
            return JobUtils.updateAsFailedJob(job)
        }

        return job
    }

}