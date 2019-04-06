package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.api.Configurator

/**
 * Configurator for toolboxes
 * @author Jakub Tucek
 */
class ToolboxConfigurator : Configurator {

    override fun configure(config: ConfigFile): ConfigFile {
        val taskSpecificToolbox = when (config.taskType) {
            is MatlabTaskType -> "matlab"
            else -> return config
        }
        val containsSpecificToolbox = config.resources.toolboxes!!.contains(taskSpecificToolbox)
        if (!containsSpecificToolbox && !taskSpecificToolbox.isBlank()) {
            return config.copy(resources = config.resources.copy(
                    toolboxes = config.resources.toolboxes + taskSpecificToolbox
            ))
        }

        return config
    }

}