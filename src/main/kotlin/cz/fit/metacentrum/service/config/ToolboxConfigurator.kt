package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.ConsoleReader
import cz.fit.metacentrum.service.api.Configurator
import javax.inject.Inject

/**
 *
 * @author Jakub Tucek
 */
class ToolboxConfigurator : Configurator {

    @Inject
    private lateinit var consoleReader: ConsoleReader

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        val taskSpecificToolbox = when (config.taskType) {
            is MatlabTaskType -> "matlab"
        }
        val containsSpecificToolbox = config.resources.toolboxes.contains(taskSpecificToolbox)
        if (containsSpecificToolbox) {
            return config
        }

        // ask user if he wants to add module
        val confirmationResult = consoleReader.askForConfirmation(
                "Looks like you are using $taskSpecificToolbox and you did specify according toolbox. Do you wish to add it?",
                true
        )
        // if yes, add it and return update configuration
        if (confirmationResult) {
            return config.copy(resources = config.resources.copy(
                    toolboxes = config.resources.toolboxes + taskSpecificToolbox
            ))
        }

        return config
    }

}