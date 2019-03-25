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

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        val taskSpecificModule = when (config.taskType) {
            is MatlabTaskType -> "matlab"
            is PythonTaskType -> "python-3.4.1-gcc"
        }
        val containsSpecificModule = config.resources.modules!!.contains(taskSpecificModule)
        if (!containsSpecificModule) {
            return config.copy(resources = config.resources.copy(
                    modules = config.resources.modules + taskSpecificModule
            ))
        }

        return config
    }

}