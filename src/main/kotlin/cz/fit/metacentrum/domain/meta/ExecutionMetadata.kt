package cz.fit.metacentrum.domain.meta

import com.fasterxml.jackson.annotation.JsonIgnore
import cz.fit.metacentrum.domain.config.ConfigFile
import java.time.LocalDateTime

/**
 * ExecutionMetadata of run task. Contains configuration that can be used for resubmitting with identical
 * configuration and status of run.
 * @author Jakub Tucek
 */
data class ExecutionMetadata(
        val configFile: ConfigFile,
        val creationTime: LocalDateTime = LocalDateTime.now(),
        val updateTime: LocalDateTime = LocalDateTime.now(),
        val paths: ExecutionMetadataPath = ExecutionMetadataPath(),
        val iterationCombinations: List<Map<String, String>>? = null,
        val jobs: List<ExecutionMetadataJob>? = null,
        val jobsHistory: List<ExecutionMetadataHistory> = emptyList(),
        val submittingUsername: String? = null,
        val metadataId: Int? = null,
        private var currentState: ExecutionMetadataState? = null
) {
    // ignore is or json deserializer thinks this is setter to field
    @JsonIgnore
    fun isFinished(): Boolean {
        return currentState == ExecutionMetadataState.DONE || currentState == ExecutionMetadataState.FAILED
    }

    @JsonIgnore
    fun getState(): ExecutionMetadataState {
        if (currentState != null && isFinished()) {
            return currentState!!
        }

        var hasFailed = false
        var hasRunning = false
        jobs!!.forEach {
            val state = it.jobInfo.state
            when (state) {
                ExecutionMetadataState.RUNNING -> hasRunning = true
                ExecutionMetadataState.INITIAL -> {
                    currentState = ExecutionMetadataState.INITIAL
                    return currentState!!
                }
                ExecutionMetadataState.FAILED -> hasFailed = true
                else -> {
                }
            }
        }
        if (hasRunning) {
            currentState = ExecutionMetadataState.RUNNING
        } else if (hasFailed) {
            currentState = ExecutionMetadataState.FAILED
        } else {
            currentState = ExecutionMetadataState.QUEUED
        }
        return currentState!!
    }

    @JsonIgnore
    fun resetState() {
        currentState = null
    }

    @JsonIgnore
    fun getJobsByState(state: ExecutionMetadataState): List<ExecutionMetadataJob> {
        return jobs!!.filter { it.jobInfo.state == state }
    }
}

object ExecutionMetadataComparator : Comparator<ExecutionMetadata> {
    override fun compare(o1: ExecutionMetadata?, o2: ExecutionMetadata?): Int {
        if (o1 == null && o2 == null) {
            return 0
        }
        if (o1 == null) return -1
        if (o2 == null) return 1
        return o1.creationTime.compareTo(o2.creationTime)
    }

}