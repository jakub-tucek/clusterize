package cz.fit.metacentrum.domain.meta

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

private const val stateDone = "METADATA_STATE_DONE"
private const val stateRunning = "METADATA_STATE_RUNNING"
private const val stateFailed = "METADATA_STATE_FAILED"

// Parent for state instances
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes(
        JsonSubTypes.Type(value = ExecutionMetadataStateDone::class, name = stateDone),
        JsonSubTypes.Type(value = ExecutionMetadataStateRunning::class, name = stateRunning),
        JsonSubTypes.Type(value = ExecutionMetadataStateFailed::class, name = stateFailed)
)
sealed class ExecutionMetadataState


// Done status. Every script finished successfully
@JsonTypeName(stateDone)
object ExecutionMetadataStateDone : ExecutionMetadataState()

// Task still running. Can contain some failed jobs that already finished.
@JsonTypeName(stateRunning)
class ExecutionMetadataStateRunning(
        val runningJobs: List<ExecutionMetadataJobRunningWrapper>,
        val queuedJobs: List<ExecutionMetadataJobRunningWrapper>,
        val failedJobs: List<ExecutionMetadataJobFailedWrapper>
) : ExecutionMetadataState()

// Failed status. Task and every job is finished but some failed - did not exit with Done status.
@JsonTypeName(stateFailed)
class ExecutionMetadataStateFailed(val failedJobs: List<ExecutionMetadataJobFailedWrapper>) : ExecutionMetadataState()


