package cz.fit.metacentrum.domain.config

import cz.fit.metacentrum.config.FileNames.defaultMetadataFolder

/**
 * Represents general configuration.
 */
data class ConfigGeneral(
        val metadataStoragePath: String = defaultMetadataFolder,
        val storagePath: String,
        val sourcesPath: String,
        val maxResubmits: Int = 0,
        val variables: Map<String, String>? = emptyMap(),
        val taskName: String? = null,
        val dependentVariables: List<ConfigGeneralDependent>
)

data class ConfigGeneralDependent(
        val name: String,
        val dependentVarName: String,
        val modifier: String
)
