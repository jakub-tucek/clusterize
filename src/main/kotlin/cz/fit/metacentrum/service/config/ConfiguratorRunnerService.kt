package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.service.api.Configurator
import javax.inject.Inject

/**
 *
 * @author Jakub Tucek
 */
class ConfiguratorRunnerService {


    @Inject
    private lateinit var configuratorList: Set<@JvmSuppressWildcards Configurator>


    fun configurate(config: ConfigFile): ConfigFile {
        return configuratorList
                .fold(config) { acc, configurator -> configurator.configureInteractively(acc) }
    }
}