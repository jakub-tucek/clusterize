package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.SerializationService
import javax.inject.Inject


/**
 * Executor that checks queue for jobs.
 * @author Jakub Tucek
 */
class CheckQueueExecutor : TaskExecutor {

    @Inject
    private lateinit var queueRecordsService: QueueRecordsService
    @Inject
    private lateinit var failedJobFinderService: FailedJobFinderService
    @Inject
    private lateinit var serializationService: SerializationService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        // TODO: Add cache
        val jobs = metadata.jobs ?: throw IllegalStateException("Jobs are missing from metadata object")

        // mapped job by pid so queue records can be mapped to jobs easily
        val mappedJobsByPid = jobs
                .map { (it.jobInfo.pid ?: throw IllegalStateException("Missing pid on task ${it}")) to it }
                .toMap()
        // read queue job records mapped by its state
        val mappedByPid = retrieveQueuedJobs(metadata, mappedJobsByPid)
        val updatedJobs = failedJobFinderService.updateJobState(jobs, mappedByPid)

        return metadata.copy(jobs = updatedJobs)
    }

    private fun retrieveQueuedJobs(metadata: ExecutionMetadata, mapedJobsByPid: Map<String, ExecutionMetadataJob>): Map<String, List<QueueRecord>> {
        val username = metadata.submittingUsername ?: throw IllegalStateException("Submitted username is missing")
        val queueList = queueRecordsService.retrieveQueueForUser(username)

        return queueList.filter { mapedJobsByPid.containsKey(it.pid) }
                .groupBy { it.pid }
    }

}