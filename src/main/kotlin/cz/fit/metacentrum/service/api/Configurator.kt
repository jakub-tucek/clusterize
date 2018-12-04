package cz.fit.metacentrum.service.api

import cz.fit.metacentrum.domain.config.ConfigFile

/**
 * Configurator is step that check config file and processes output to be ready for jobs execution.
 * @author Jakub Tucek
 */
interface Configurator {

    fun configureInteractively(config: ConfigFile): ConfigFile

}