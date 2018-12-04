package cz.fit.metacentrum.domain.config

/**
 * Representation of configuration file
 * @author Jakub Tucek
 */
data class ConfigFile(
        val iterations: List<ConfigIteration>,
        val general: ConfigGeneral,
        val taskType: TaskType,
        val resources: ConfigResources
)
