package cz.fit.metacentrum.domain.template

data class TemplateResources(
        val walltime: String,
        val formattedResources: String, // formatted resources
        val ncpus: Int,
        val modules: Set<String>, // name of loaded module
        val toolboxes: Set<String> // name of used toolboxes
)
