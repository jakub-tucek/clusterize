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
class ModulesConfigurator : Configurator {

    @Inject
    private lateinit var consoleReader: ConsoleReader

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        val taskSpecificModule = when (config.taskType) {
            is MatlabTaskType -> "matlab"
        }
        val containsSpecificModule = config.resources.modules.contains(taskSpecificModule)
        if (containsSpecificModule) {
            return config
        }

        // ask user if he wants to add module
        val confirmationResult = consoleReader.askForConfirmation(
                "Looks like you are using $taskSpecificModule and you did specify proper module. Do you wish to add it?",
                true
        )
        // if yes, add it and return update configuration
        if (confirmationResult) {
            return config.copy(resources = config.resources.copy(
                    modules = config.resources.modules + taskSpecificModule
            ))
        }

        return config
    }

}