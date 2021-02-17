package cz.fit.metacentrum.domain.config


// defines config resources
data class ConfigResources(
        val profile: ConfigResourceProfile,
        val resourceType: String? = null, // must be filled only for auto mode
        val details: ConfigResourcesDetails? = null,
        val modules: Set<String>? = emptySet(),
        val toolboxes: Set<String>? = emptySet()
)

data class ConfigResourcesDetails(
        val chunks: Int,
        val walltime: String,
        val mem: String,
        val ncpus: Int,
        val scratchLocal: String,
        val ngpus: Int? = null,
        val cpuFlag: String? = null
)

enum class ConfigResourceProfile {
    CUSTOM,
    AUTO
}
