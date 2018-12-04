package cz.fit.metacentrum.domain.config


// defines config resources
data class ConfigResources(
        val profile: String,
        val details: ConfigResourcesDetails? = null
)

data class ConfigResourcesDetails(
        val chunks: String,
        val walltime: String,
        val mem: String,
        val ncpus: Int
)

