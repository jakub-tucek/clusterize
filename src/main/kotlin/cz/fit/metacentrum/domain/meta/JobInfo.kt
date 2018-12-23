package cz.fit.metacentrum.domain.meta

import java.time.LocalDateTime

/**
 * Represent additional info about job run.
 * Can be created artificially with partial info (such as pid when job is submitted) or parsed
 * from file where all data are stored as job is progressing its state (from queued, to running, to finish).
 * @author Jakub Tucek
 */
data class JobInfo(
        val start: LocalDateTime?,
        val end: LocalDateTime?,
        val pid: String?,
        val status: Int?,
        val output: String?,
        val runningTime: String?,
        val state: ExecutionMetadataState = ExecutionMetadataState.QUEUED
) {
    companion object {
        fun empty(): JobInfo {
            return JobInfo(null, null, null, null, null, null)
        }
    }
}

object JobInfoFilePropNames {
    const val start = "start"
    const val end = "end"
    const val pid = "pid"
    const val status = "status"
}