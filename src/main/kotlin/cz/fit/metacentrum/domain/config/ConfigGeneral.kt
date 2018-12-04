package cz.fit.metacentrum.domain.config

/**
 * Represents general configuration.
 */
data class ConfigGeneral(
        val metadataStoragePath: String,
        val storagePath: String,
        val sourcesPath: String,
        val variables: Map<String, String>? = emptyMap(),
        val taskName: String? = null,
        val dependents: List<ConfigGeneralDependent>
)

data class ConfigGeneralDependent(
        val name: String,
        val dependentVarName: String,
        val modifier: String
)
