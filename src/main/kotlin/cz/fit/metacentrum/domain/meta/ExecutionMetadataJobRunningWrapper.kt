package cz.fit.metacentrum.domain.meta

// Wrapping object for running job
data class ExecutionMetadataJobRunningWrapper(
        val job: ExecutionMetadataJob,
        val runTime: String
)