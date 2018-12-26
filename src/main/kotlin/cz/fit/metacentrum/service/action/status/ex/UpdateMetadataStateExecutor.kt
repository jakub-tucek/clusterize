package cz.fit.metacentrum.service.action.status.ex

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.api.TaskExecutor

/**
 * Updates task state based on jobs state.
 * @author Jakub Tucek
 */
class UpdateMetadataStateExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val newState = getState(metadata.jobs, metadata.currentState)

        return if (metadata.currentState != newState) {
            metadata.copy(currentState = newState)
        } else {
            metadata
        }
    }


    private fun getState(jobs: List<ExecutionMetadataJob>?, currentState: ExecutionMetadataState): ExecutionMetadataState {
        // state is finishing, dont need to check again
        if (currentState.isFinishing()) {
            return currentState
        }

        var hasFailed = false
        var hasRunning = false
        var hasQueued = false
        jobs!!.forEach {
            val state = it.jobInfo.state
            when (state) {
                ExecutionMetadataState.RUNNING -> hasRunning = true
                ExecutionMetadataState.FAILED -> hasFailed = true
                ExecutionMetadataState.QUEUED -> hasQueued = true
                else -> {
                    // nothing
                }
            }
        }
        return when {
            hasRunning -> ExecutionMetadataState.RUNNING // if it has running, it is running
            hasFailed -> ExecutionMetadataState.FAILED // if it has no running but have failed, it is failed
            hasQueued -> ExecutionMetadataState.QUEUED // has no running, no failed and has queued task - it is queued
            else -> ExecutionMetadataState.DONE // otherwise its done
        }
    }
}