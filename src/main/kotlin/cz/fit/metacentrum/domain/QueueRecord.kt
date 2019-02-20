package cz.fit.metacentrum.domain

/**
 * Representation of 1 queue record of job from qstat -u xxxx
 * @author Jakub Tucek
 */
data class QueueRecord(
        val pid: String,
        val username: String,
        val queueName: String,
        val jobName: String,
        val sessionId: String,
        val nds: String,
        val tsk: String,
        val requiredMemory: String,
        val requiredTime: String,
        val internalState: InternalState,
        val elapsedTime: String,
        val state: State
) {
    // state of job - should be Q(queued) or R(running). E is for errored job.
    enum class InternalState() {
        E, //Job is	exiting	after having run.
        H, //Job is	held.
        Q, //job is	queued,	eligable to run	or routed.
        R, //job is	running.
        T, //job is	being moved to new location.
        W, //job is	waiting	for its	execution time
        S, //(Unicos only) job is suspend.
        U // unknown
    }

    enum class State() {
        RUNNING, QUEUED
    }
}
