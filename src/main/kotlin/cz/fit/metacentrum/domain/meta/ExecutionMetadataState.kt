package cz.fit.metacentrum.domain.meta


enum class ExecutionMetadataState {
    DONE,
    RUNNING,
    FAILED,
    QUEUED,
    INITIAL; // Default state (substitutes null value)

    // ignore is or json deserializer thinks this is setter to field
    fun isFinishing(): Boolean {
        return this == DONE || this == FAILED
    }
}


