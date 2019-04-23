package cz.fit.metacentrum.domain.template

import cz.fit.metacentrum.domain.config.ConfigGeneralDependent

// General information about task
data class GeneralTemplateData(
        val taskName: String?,
        val dependentVariables: List<ConfigGeneralDependent>
)