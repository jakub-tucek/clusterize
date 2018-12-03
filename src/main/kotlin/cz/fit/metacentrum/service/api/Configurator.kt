package cz.fit.metacentrum.service.api

import cz.fit.metacentrum.domain.config.ConfigFile

/**
 *
 * @author Jakub Tucek
 */
interface Configurator {

    fun configureInteractively(config: ConfigFile): ConfigFile

}