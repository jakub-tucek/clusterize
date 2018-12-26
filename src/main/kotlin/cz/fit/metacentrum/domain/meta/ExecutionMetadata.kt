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
        val currentState: ExecutionMetadataState = ExecutionMetadataState.INITIAL
) {


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