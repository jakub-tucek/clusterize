package cz.fit.metacentrum.domain

/**
 * Represents environment where file is run
 */
data class ConfigEnvironment(
        val homeDir: String,
        val variables: Map<String, String>
)