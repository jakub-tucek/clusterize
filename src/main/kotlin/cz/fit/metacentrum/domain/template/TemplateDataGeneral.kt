package cz.fit.metacentrum.domain.template

import cz.fit.metacentrum.domain.config.ConfigGeneralDependent

data class TemplateDataGeneral(
        val taskName: String?,
        val dependents: List<ConfigGeneralDependent>
)