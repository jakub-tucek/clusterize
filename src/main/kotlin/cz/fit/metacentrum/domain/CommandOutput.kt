package cz.fit.metacentrum.domain

/**
 *
 * @author Jakub Tucek
 */
data class CommandOutput(val output: String, val status: Int, val errOutput: String) {
    fun mergedOut(): String {
        return output + errOutput
    }
}