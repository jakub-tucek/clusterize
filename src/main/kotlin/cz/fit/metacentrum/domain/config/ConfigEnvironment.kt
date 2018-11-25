package cz.fit.metacentrum.domain.config

/**
 * Represents environment where file is run
 */
data class ConfigEnvironment(
        val basePath: String,
        val variables: Map<String, String>,
        val dependents: List<ConfigEnvironmentDependent>
)

data class ConfigEnvironmentDependent(
        val name: String,
        val dependentVarName: String,
        val modifier: String
)