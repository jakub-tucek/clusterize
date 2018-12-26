package cz.fit.metacentrum.service.action.status.ex

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.JobUtils

/**
 * Checks for status job based on exit status.
 * @author Jakub Tucek
 */
class JobStatusCheckExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val updatedJobState = metadata.jobs!!
                .map {
                    when {
                        it.jobInfo.state.isFinishing() -> it
                        it.jobInfo.status == null -> it
                        it.jobInfo.status == 0 -> it.copy(jobInfo = it.jobInfo.copy(state = ExecutionMetadataState.DONE))
                        else -> JobUtils.updateAsFailedJob(it)
                    }
                }
        return metadata.copy(jobs = updatedJobState)
    }
}