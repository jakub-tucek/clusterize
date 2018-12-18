package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.service.ConsoleReader
import cz.fit.metacentrum.service.api.Configurator
import javax.inject.Inject

/**
 * Configurator for task name
 * @author Jakub Tucek
 */
class TaskNameConfigurator : Configurator {


    @Inject
    private lateinit var consoleReader: ConsoleReader

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        val jobName = config.general.taskName
        if (jobName != null) return config

        val defaultName = config.taskType::class.simpleName
        val newJobName = consoleReader.askForValue(
                "Seems like you did not specify task name. Please enter task name [${defaultName}]:"
        ) { s -> if (s.ifBlank { null } == null) defaultName else s }

        return config.copy(general = config.general.copy(taskName = newJobName))
    }
}