package cz.fit.metacentrum.domain.meta

import java.time.LocalDateTime

/**
 * Represent file where script outputs information such as when it was executed, when it ended etc.
 * @author Jakub Tucek
 */
data class JobInfo(
        val start: LocalDateTime?,
        val end: LocalDateTime?,
        val pid: String?,
        val status: Int?
) {
    companion object {
        fun empty(): JobInfo {
            return JobInfo(null, null, null, null)
        }
    }
}

object JobInfoFilePropNames {
    const val start = "start"
    const val end = "end"
    const val pid = "pid"
    const val status = "status"
}