package cz.fit.metacentrum.domain.config


data class ConfigResources(
        val profile: String,
        val details: ConfigResourcesDetails?
)

data class ConfigResourcesDetails(
        val chunks: String,
        val walltime: String,
        val mem: String,
        val ncpus: Int
)

