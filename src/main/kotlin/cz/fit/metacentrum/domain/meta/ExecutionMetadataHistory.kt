package cz.fit.metacentrum.domain.meta

// represents executed jobs in history
// meaning that result of those jobs is ignored and rerun task upon them was submitted
data class ExecutionMetadataHistory(
        val pastJobs: List<ExecutionMetadataJob>
)