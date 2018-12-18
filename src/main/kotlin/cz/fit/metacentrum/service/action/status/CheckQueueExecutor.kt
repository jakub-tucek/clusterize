package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.meta.*
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
        if (checkIfFinishedQueueWasProcessed(metadata)) {
            // no need to check status again
            return metadata
        }
        val jobs = metadata.jobs ?: throw IllegalStateException("Jobs are missing from metadata object")

        // mapped job by pid so queue records can be mapped to jobs easily
        val mappedJobsByPid = jobs
                .map { (it.jobInfo.pid ?: throw IllegalStateException("Missing pid on task ${it}")) to it }
                .toMap()
        // read queue job records mapped by its state
        val mappedRecordsByState = retrieveQueuedJobs(metadata, mappedJobsByPid)

        val queued = mappedRecordsByState[QueueRecord.State.QUEUED] ?: emptyList()
        val running = mappedRecordsByState[QueueRecord.State.RUNNING] ?: emptyList()
        val runningQueuedPids = (queued + running).map { it.pid }
        val failedJobs = failedJobFinderService.findFailedJobs(jobs, runningQueuedPids)

        // no running jobs - so it either failed or finished successfully
        if (running.isEmpty() && queued.isEmpty()) {
            if (failedJobs.isEmpty()) {
                return metadata.copy(state = ExecutionMetadataStateDone)
            } else {
                return metadata.copy(state = ExecutionMetadataStateFailed(
                        failedJobs
                ))
            }
        } else {
            // running task, some jobs maybe finished with Done or fail status
            return metadata.copy(
                    state = ExecutionMetadataStateRunning(
                            runningJobs = mapRecordsToRunningJob(running, mappedJobsByPid),
                            queuedJobs = mapRecordsToRunningJob(queued, mappedJobsByPid),
                            failedJobs = failedJobs
                    )
            )
        }
    }

    private fun mapRecordsToRunningJob(running: List<QueueRecord>, mappedJobsByPid: Map<String, ExecutionMetadataJob>): List<ExecutionMetadataJobRunningWrapper> {
        return running.map {
            val job = mappedJobsByPid[it.pid] ?: throw IllegalStateException("Mapped pid does not exist")
            ExecutionMetadataJobRunningWrapper(
                    job = job,
                    runTime = it.elapsedTime
            )
        }

    }

    private fun retrieveQueuedJobs(metadata: ExecutionMetadata, mapedJobsByPid: Map<String, ExecutionMetadataJob>): Map<QueueRecord.State, List<QueueRecord>> {
        val username = metadata.submittingUsername ?: throw IllegalStateException("Submitted username is missing")
        val queueList = queueRecordsService.retrieveQueueForUser(username)

        return queueList.filter { mapedJobsByPid.containsKey(it.pid) }
                .groupBy { it.state }
    }

    private fun checkIfFinishedQueueWasProcessed(metadata: ExecutionMetadata): Boolean {
        return when (metadata.state) {
            is ExecutionMetadataStateDone -> true
            is ExecutionMetadataStateFailed -> true
            else -> false
        }
    }


}