package cz.fit.metacentrum.domain.config

import com.fasterxml.jackson.annotation.JsonIgnore
import cz.fit.metacentrum.config.FileNames.defaultMetadataFolder

/**
 * Represents general configuration.
 */
data class ConfigGeneral(
        val metadataStoragePath: String = defaultMetadataFolder,
        val storagePath: String,
        val sourcesPath: String,
        val maxResubmits: Int = 0,
        @JsonIgnore
        val variables: Map<String, String> = emptyMap(),
        val taskName: String? = null,
        @JsonIgnore
        val dependentVariables: List<ConfigGeneralDependent> = emptyList()
)

data class ConfigGeneralDependent(
        val name: String,
        val dependentVarName: String,
        val modifier: String
)
