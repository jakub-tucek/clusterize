package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.service.ConsoleReader
import cz.fit.metacentrum.service.api.Configurator
import javax.inject.Inject

/**
 *
 * @author Jakub Tucek
 */
class ResourcesConfigurator : Configurator {

    @Inject
    private lateinit var consoleReader: ConsoleReader

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        if (config.resources.details == null) {
//            consoleReader.askForConfirmation()
        }
//        TODO()
        return config
    }
}