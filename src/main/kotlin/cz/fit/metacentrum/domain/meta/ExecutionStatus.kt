package cz.fit.metacentrum.domain.meta


// Parent for state instances
sealed class ExecutionMetadataState


// Ok status. Every script finished successfully
object ExecutionMetadataStateOk : ExecutionMetadataState()

// Task still running. Can contain some failed jobs that already finished.
class ExecutionMetadataStateRunning(
        val runningJobs: List<ExecutionMetadataRunningJob>,
        val queuedJobs: List<ExecutionMetadataRunningJob>,
        val failedJobs: List<ExecutionMetadataFailedJob>
) : ExecutionMetadataState()

// Failed status. Task and every job is finished but some failed - did not exit with OK status.
class ExecutionMetadataStateFailed(val failedJobs: List<ExecutionMetadataFailedJob>) : ExecutionMetadataState()


// Wrapping object for failed job
data class ExecutionMetadataFailedJob(
        val scriptJob: ExecutionMetadataScriptJob
)

// Wrapping object for running job
data class ExecutionMetadataRunningJob(
        val scriptJob: ExecutionMetadataScriptJob,
        val runTime: String
)
