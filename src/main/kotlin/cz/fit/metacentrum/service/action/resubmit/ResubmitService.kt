package cz.fit.metacentrum.service.action.resubmit

import com.google.inject.name.Named
import cz.fit.metacentrum.config.actionResubmitToken
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.SubmitRunner
import cz.fit.metacentrum.service.api.TaskExecutor
import mu.KotlinLogging
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

/**
 * Resubmit service that provides methods for execution and preparation metadata for resubmit.
 * @author Jakub Tucek
 */
class ResubmitService {

    @Inject
    @Named(actionResubmitToken)
    private lateinit var resubmitExecutors: Set<@JvmSuppressWildcards TaskExecutor>

    @Inject
    private lateinit var submitRunner: SubmitRunner

    /**
     * Checks jobs if they are ready for resubmit. If yes - performs resubmit.
     * @return new metadata if some jobs were resubmitted or null
     */
    fun checkJobsForResubmit(metadata: ExecutionMetadata): ExecutionMetadata? {
        // prepare jobs for resubmit -> only failed jobs and if max resubmit quota is not exceeded
        val preparedMetadata = prepareForResubmit(metadata) {
            it.jobInfo.state == ExecutionMetadataState.FAILED
                    && it.resubmitCounter < metadata.configFile.general.maxResubmits
        }
        // history not changed, no jobs to resubmit
        if (preparedMetadata.currentState == metadata.currentState) {
            logger.debug { "No jobs were considered for resubmit for metadata ${metadata.paths.metadataStoragePath}" }
            return null
        }
        logger.debug { "Resubmitting jobs for metadata ${metadata.paths.metadataStoragePath}" }
        executeResubmit(preparedMetadata)
        return preparedMetadata
    }

    /**
     * Runs executors for given metadata based on matlab task type
     */
    fun executeResubmit(preparedMetadata: ExecutionMetadata) {
        // resubmit is same of all types so just - RUN IT
        submitRunner.run(preparedMetadata, resubmitExecutors)
    }

    /**
     * Updates metadata so when executed, it will rerun only failed jobs. This is done by copying failed jobs
     * to history and setting job state to INITIAL.
     */
    fun prepareForResubmit(metadata: ExecutionMetadata, shouldResubmitPredicate: (ExecutionMetadataJob) -> Boolean): ExecutionMetadata {
        var jobChanged = false
        val jobs = metadata.jobs!!.map {
            if (shouldResubmitPredicate(it)) {
                jobChanged = true
                it.copy(jobInfo = it.jobInfo.copy(
                        state = ExecutionMetadataState.INITIAL),
                        resubmitCounter = it.resubmitCounter + 1,
                        jobParent = it
                )
            } else {
                it
            }
        }
        return metadata.copy(
                jobs = jobs,
                currentState = if (jobChanged) ExecutionMetadataState.INITIAL else metadata.currentState,
                totalResubmits = if (jobChanged) metadata.totalResubmits + 1 else metadata.totalResubmits
        )
    }
}