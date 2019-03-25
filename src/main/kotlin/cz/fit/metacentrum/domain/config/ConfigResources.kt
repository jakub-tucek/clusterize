package cz.fit.metacentrum.domain.config

import com.fasterxml.jackson.annotation.JsonIgnore


// defines config resources
data class ConfigResources(
        val profile: ConfigResourceProfile,
        val resourceType: String? = null, // must be filled only for auto mode
        val details: ConfigResourcesDetails? = null,
        @JsonIgnore
        val modules: Set<String> = emptySet(),
        @JsonIgnore
        val toolboxes: Set<String> = emptySet()
)

data class ConfigResourcesDetails(
        val chunks: Int,
        val walltime: String,
        val mem: String,
        val ncpus: Int,
        val scratchLocal: String,
        val ngpus: Int? = null
)

enum class ConfigResourceProfile {
    CUSTOM,
    AUTO
}
