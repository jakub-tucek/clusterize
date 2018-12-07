package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.service.api.Configurator
import javax.inject.Inject

/**
 *
 * @author Jakub Tucek
 */
class ConfiguratorRunner {


    @Inject
    private lateinit var configuratorList: Set<@JvmSuppressWildcards Configurator>


    fun configure(config: ConfigFile): ConfigFile {
        return configuratorList
                .fold(config) { acc, configurator -> configurator.configureInteractively(acc) }
    }
}