package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.service.api.Configurator

/**
 *
 * @author Jakub Tucek
 */
class TaskNameConfigurator : Configurator {

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        var jobName = config.environment.taskName
        while (jobName == null) {
            println("Seems like you did not specify task name. Please enter task name [NAME]:")
            jobName = readLine()?.ifBlank { null }
        }
        if (jobName.isNullOrBlank()) {
            jobName = config.taskType::class.simpleName
        }
        return config.copy(environment = config.environment.copy(taskName = jobName))
    }
}