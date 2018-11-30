package cz.fit.metacentrum.domain

/**
 * Representation of 1 queue record of job.
 * @author Jakub Tucek
 */
data class QueueRecord(
        val pid: String,
        val jobName: String,
        val username: String,
        val timestamp: String,
        val state: State,
        val queueType: String
) {
    // state of job - should be Q(queued) or R(running)
    enum class State() {
        R, Q
    }
}
