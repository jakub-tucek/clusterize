package cz.fit.metacentrum.domain.meta


sealed class ExecutionMetadataState


object ExecutionMetadataStateOk : ExecutionMetadataState()

class ExecutionMetadataStateRunning(
        val runningJobs: List<ExecutionMetadataRunningJob>,
        val failedJobs: List<ExecutionMetadataFailedJob>
) : ExecutionMetadataState()

class ExecutionMetadataStateFailed(
        val failedJobs: List<ExecutionMetadataFailedJob>
) : ExecutionMetadataState()


data class ExecutionMetadataFailedJob(
        val scriptJob: ExecutionMetadataScriptJob
)

data class ExecutionMetadataRunningJob(
        val scriptJob: ExecutionMetadataScriptJob,
        val runTime: String
)
