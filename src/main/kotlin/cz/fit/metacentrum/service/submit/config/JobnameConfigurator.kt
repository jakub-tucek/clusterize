package cz.fit.metacentrum.service.submit.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.service.api.Configurator

/**
 *
 * @author Jakub Tucek
 */
class JobnameConfigurator : Configurator {

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        var jobName = config.general.jobName
        while (jobName == null) {
            println("Seems like you did not specify job name. Please enter job name [NAME]?")
            jobName = readLine()
        }
        return config.copy(general = config.general.copy(jobName = jobName))
    }
}