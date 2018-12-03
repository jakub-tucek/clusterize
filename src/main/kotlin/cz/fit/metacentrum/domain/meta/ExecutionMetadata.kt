package cz.fit.metacentrum.domain.meta

import cz.fit.metacentrum.domain.config.ConfigFile
import java.time.LocalDateTime

/**
 *
 * @author Jakub Tucek
 */
data class ExecutionMetadata(
        val configFile: ConfigFile,
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val paths: ExecutionMetadataPath = ExecutionMetadataPath(),
        val iterationCombinations: List<Map<String, String>>? = null,
        val jobs: List<ExecutionMetadataJob>? = null,
        val submittingUsername: String? = null,
        val state: ExecutionMetadataState? = null
)

object ExecutionMetadataComparator : Comparator<ExecutionMetadata> {
    override fun compare(o1: ExecutionMetadata?, o2: ExecutionMetadata?): Int {
        if (o1 == null && o2 == null) {
            return 0
        }
        if (o1 == null) return -1
        if (o2 == null) return 1
        return o1.timestamp.compareTo(o2.timestamp)
    }

}