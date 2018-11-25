package cz.fit.metacentrum.domain

/**
 *
 * @author Jakub Tucek
 */
data class TemplateData(
        val taskType: TaskType,
        val variables: List<Pair<String, String>>,
        val functionParams: String
)