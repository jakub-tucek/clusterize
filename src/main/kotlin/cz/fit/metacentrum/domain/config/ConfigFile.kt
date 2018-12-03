package cz.fit.metacentrum.domain.config

/**
 * Representation of configuration file
 * @author Jakub Tucek
 */
data class ConfigFile(
        val iterations: List<ConfigIteration>,
        val environment: ConfigEnvironment,
        val taskType: TaskType,
        val general: ConfigGeneral = ConfigGeneral()
)
