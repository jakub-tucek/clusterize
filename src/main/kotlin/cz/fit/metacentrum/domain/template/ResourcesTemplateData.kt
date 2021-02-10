package cz.fit.metacentrum.domain.template

// Resources where script is executed
data class ResourcesTemplateData(
        val walltime: String,
        val formattedResources: String, // formatted resources
        val ncpus: Int,
        val gpuQueue: String?,
        val modules: Set<String>, // name of loaded module
        val toolboxes: Set<String> // name of used toolboxes
)
