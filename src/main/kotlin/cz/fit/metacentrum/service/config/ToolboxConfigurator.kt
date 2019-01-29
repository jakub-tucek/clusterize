package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.config.PythonTaskType
import cz.fit.metacentrum.service.ConsoleReader
import cz.fit.metacentrum.service.api.Configurator
import javax.inject.Inject

/**
 * Configurator for toolboxes
 * @author Jakub Tucek
 */
class ToolboxConfigurator : Configurator {

    @Inject
    private lateinit var consoleReader: ConsoleReader

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        val taskSpecificToolbox = when (config.taskType) {
            is MatlabTaskType -> "matlab"
            is PythonTaskType -> "python"
        }
        val containsSpecificToolbox = config.resources.toolboxes.contains(taskSpecificToolbox)
        if (!containsSpecificToolbox) {
            return config.copy(resources = config.resources.copy(
                    toolboxes = config.resources.toolboxes + taskSpecificToolbox
            ))
        }

        return config
    }

}