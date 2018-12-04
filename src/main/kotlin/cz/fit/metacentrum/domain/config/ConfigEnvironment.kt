package cz.fit.metacentrum.domain.config

/**
 * Represents environment where file is run
 */
data class ConfigEnvironment(
        val metadataStoragePath: String,
        val storagePath: String,
        val sourcesPath: String,
        val variables: Map<String, String>? = emptyMap(),
        val taskName: String? = null,
        val dependents: List<ConfigEnvironmentDependent>
)

data class ConfigEnvironmentDependent(
        val name: String,
        val dependentVarName: String,
        val modifier: String
)
