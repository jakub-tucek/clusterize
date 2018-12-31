package cz.fit.metacentrum.service.action.resubmit

import com.google.inject.name.Named
import cz.fit.metacentrum.config.actionResubmitMatlabExecutorsTokens
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataHistory
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.SubmitRunner
import cz.fit.metacentrum.service.api.TaskExecutor
import javax.inject.Inject

/**
 * Resubmit service that provides methods for execution and preparation metadata for resubmit.
 * @author Jakub Tucek
 */
class ResubmitService {

    @Inject
    @Named(actionResubmitMatlabExecutorsTokens)
    private lateinit var matlabResubmitExecutors: Set<@JvmSuppressWildcards TaskExecutor>

    @Inject
    private lateinit var submitRunner: SubmitRunner

    /**
     * Checks jobs if they are ready for resubmit. If yes - performs resubmit.
     */
    fun checkJobsForResubmit(metadata: ExecutionMetadata) {
        val previousHistorySize = metadata.jobsHistory.size
        // prepare jobs for resubmit -> only failed jobs and if max resubmit quota is not exceeded
        val preparedMetadata = prepareForResubmit(metadata) {
            it.jobInfo.state == ExecutionMetadataState.FAILED
                    && it.resubmitCounter < metadata.configFile.general.maxResubmits
        }
        // history not changed, no jobs to resubmit
        if (previousHistorySize == preparedMetadata.jobsHistory.size) {
            return
        }
        executeResubmit(preparedMetadata)

    }

    /**
     * Runs executors for given metadata based on matlab task type
     */
    fun executeResubmit(preparedMetadata: ExecutionMetadata) {
        when (preparedMetadata.configFile.taskType) {
            is MatlabTaskType -> submitRunner.run(preparedMetadata, matlabResubmitExecutors)
        }
    }

    /**
     * Updates metadata so when executed, it will rerun only failed jobs. This is done by copying failed jobs
     * to history and setting job state to INITIAL.
     */
    fun prepareForResubmit(metadata: ExecutionMetadata, shouldResubmitPredicate: (ExecutionMetadataJob) -> Boolean): ExecutionMetadata {
        val pastJobs: MutableList<ExecutionMetadataJob> = mutableListOf()
        val jobs = metadata.jobs!!.map {
            if (shouldResubmitPredicate(it)) {
                pastJobs.add(it)
                it.copy(jobInfo = it.jobInfo.copy(state = ExecutionMetadataState.INITIAL), resubmitCounter = it.resubmitCounter + 1)
            } else {
                it
            }
        }
        return metadata.copy(
                jobs = jobs,
                jobsHistory = metadata.jobsHistory + ExecutionMetadataHistory(pastJobs = pastJobs)
        )
    }
}