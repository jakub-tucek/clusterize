package cz.fit.metacentrum.domain

/**
 * Representation of configuration file
 * @author Jakub Tucek
 */
data class ConfigurationFile(
        val variables: Map<String, String>
) {
}