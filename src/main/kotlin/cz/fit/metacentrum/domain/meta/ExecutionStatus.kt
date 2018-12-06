package cz.fit.metacentrum.domain.meta


// Parent for state instances
sealed class ExecutionMetadataState


// Done status. Every script finished successfully
object ExecutionMetadataStateDone : ExecutionMetadataState()

// Task still running. Can contain some failed jobs that already finished.
class ExecutionMetadataStateRunning(
        val runningJobs: List<ExecutionMetadataJobRunningWrapper>,
        val queuedJobs: List<ExecutionMetadataJobRunningWrapper>,
        val failedJobs: List<ExecutionMetadataJobFailedWrapper>
) : ExecutionMetadataState()

// Failed status. Task and every job is finished but some failed - did not exit with Done status.
class ExecutionMetadataStateFailed(val failedJobs: List<ExecutionMetadataJobFailedWrapper>) : ExecutionMetadataState()


// Wrapping object for failed job
data class ExecutionMetadataJobFailedWrapper(
        val job: ExecutionMetadataJob,
        val status: Int?,
        val output: String
)

// Wrapping object for running job
data class ExecutionMetadataJobRunningWrapper(
        val job: ExecutionMetadataJob,
        val runTime: String
)
