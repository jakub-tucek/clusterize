package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.config.PythonTaskType
import cz.fit.metacentrum.service.ConsoleReader
import cz.fit.metacentrum.service.api.Configurator
import javax.inject.Inject

/**
 * Configurator for resources - modules
 * @author Jakub Tucek
 */
class ModulesConfigurator : Configurator {

    @Inject
    private lateinit var consoleReader: ConsoleReader

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        val taskSpecificModule = when (config.taskType) {
            is MatlabTaskType -> "matlab"
            is PythonTaskType -> "python"
        }
        val containsSpecificModule = config.resources.modules.contains(taskSpecificModule)
        if (!containsSpecificModule) {
            return config.copy(resources = config.resources.copy(
                    modules = config.resources.modules + taskSpecificModule
            ))
        }

        return config
    }

}