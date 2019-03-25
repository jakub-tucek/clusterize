package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.config.PythonTaskType
import cz.fit.metacentrum.service.api.Configurator

/**
 * Configurator for toolboxes
 * @author Jakub Tucek
 */
class ToolboxConfigurator : Configurator {

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        val taskSpecificToolbox = when (config.taskType) {
            is MatlabTaskType -> "matlab"
            is PythonTaskType -> ""
        }
        val containsSpecificToolbox = config.resources.toolboxes.contains(taskSpecificToolbox)
        if (!containsSpecificToolbox && !taskSpecificToolbox.isBlank()) {
            return config.copy(resources = config.resources.copy(
                    toolboxes = config.resources.toolboxes + taskSpecificToolbox
            ))
        }

        return config
    }

}