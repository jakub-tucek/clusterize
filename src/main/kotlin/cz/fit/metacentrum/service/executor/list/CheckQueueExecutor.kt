package cz.fit.metacentrum.service.executor.list

import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.meta.*
import cz.fit.metacentrum.service.QueueService
import cz.fit.metacentrum.service.api.TaskExecutor
import java.nio.file.Files
import java.nio.file.Path
import javax.inject.Inject
import kotlin.streams.toList

/**
 * Executor that checks queue for jobs.
 * @author Jakub Tucek
 */
class CheckQueueExecutor : TaskExecutor {

    @Inject
    private lateinit var queueService: QueueService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        if (checkIfFinishedQueueWasProcessed(metadata)) {
            // no need to check status again
            return metadata
        }

        val mappedRecordsByState = retrieveQueuedJobs(metadata)

        val queued = mappedRecordsByState[QueueRecord.State.Q] ?: emptyList()
        val running = mappedRecordsByState[QueueRecord.State.R] ?: emptyList()
        val checkFailedJobs = checkFailedJobs(metadata)


        return metadata
    }

    private fun checkFailedJobs(metadata: ExecutionMetadata): List<ExecutionMetadataJobFailedWrapper> {
        val storagePath = metadata.storagePath
                ?: throw IllegalStateException("Storage path is missing in metadata object")
        val jobs = metadata.jobs ?: throw IllegalStateException("Jobs are missing from metadata object")
        Files.list(storagePath)
                .toList()
                .mapIndexed { index, path -> checkForErrorInPath(path, jobs[index]) }

    }

    private fun checkForErrorInPath(path: Path, executionMetadataJob: ExecutionMetadataJob): Any {

    }

    private fun retrieveQueuedJobs(metadata: ExecutionMetadata): Map<QueueRecord.State, List<QueueRecord>> {
        val username = metadata.submittingUsername ?: throw IllegalStateException("Submitted username is missing")
        val queueList = queueService.retrieveQueueForUser(username)
        val mappedJobsByPid = metadata.jobs
                ?.map { (it.pid ?: throw IllegalStateException("Missing pid on task ${it}")) to it }
                ?.toMap()
                ?: throw IllegalStateException("Jobs are missing from metadata")

        return queueList.filter { mappedJobsByPid.containsKey(it.pid) }
                .groupBy { it.state }
    }

    private fun checkIfFinishedQueueWasProcessed(metadata: ExecutionMetadata): Boolean {
        when (metadata.state) {
            is ExecutionMetadataStateOk -> return true
            is ExecutionMetadataStateFailed -> return true
            else -> return false
        }
    }

}