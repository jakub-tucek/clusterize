package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.config.PythonTaskType
import cz.fit.metacentrum.service.api.Configurator

/**
 * Configurator for resources - modules
 * @author Jakub Tucek
 */
class ModulesConfigurator : Configurator {

    override fun configure(config: ConfigFile): ConfigFile {
        val taskSpecificModule = when (config.taskType) {
            is MatlabTaskType -> "matlab"
            is PythonTaskType -> "python-3.6.2-gcc"
        }
        val taskSpecificPattern = when (config.taskType) {
            is MatlabTaskType -> "^matlab-[0-9]+.[0-9]+$".toRegex()
            is PythonTaskType -> "^python-[0-9]+.[0-9]+.[0-9]+-gcc$".toRegex()
        }

        val anyMatches = config.resources.modules!!.find { it.matches(taskSpecificPattern) }

        if (anyMatches != null) return config;

        return config.copy(resources = config.resources.copy(
                modules = config.resources.modules + taskSpecificModule
        ))

    }

}