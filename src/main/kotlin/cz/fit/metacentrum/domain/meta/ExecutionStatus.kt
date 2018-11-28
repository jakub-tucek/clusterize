package cz.fit.metacentrum.domain.meta

/**
 *
 * @author Jakub Tucek
 */


data class ExecutionMetadataStatus(
        val state: State
) {

    enum class State() {
        OK, RUNNING, FAILED
    }
}
