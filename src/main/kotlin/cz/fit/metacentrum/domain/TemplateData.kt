package cz.fit.metacentrum.domain

import cz.fit.metacentrum.domain.config.TaskType

/**
 *
 * @author Jakub Tucek
 */
data class TemplateData(
        val taskType: TaskType,
        val variables: List<Pair<String, String>>,
        val functionParams: String
)