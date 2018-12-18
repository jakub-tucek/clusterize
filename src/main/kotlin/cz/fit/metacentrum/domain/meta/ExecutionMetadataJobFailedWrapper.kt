package cz.fit.metacentrum.domain.meta

// Wrapping object for failed job
data class ExecutionMetadataJobFailedWrapper(
        val job: ExecutionMetadataJob,
        val output: String
)