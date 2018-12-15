package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.config.ProfileConfiguration
import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.ConfigResourcesDetails
import cz.fit.metacentrum.service.api.Configurator
import mu.KotlinLogging


private val logging = KotlinLogging.logger {}

/**
 * Clean up configuration for dev mode where toolboxes and modules are not set.
 * @author Jakub Tucek
 */
class DevelopmentCleanerConfigurator : Configurator {
    override fun configureInteractively(config: ConfigFile): ConfigFile {
        if (!ProfileConfiguration.isDev()) {
            logging.warn("DevelopmentCleanerConfigurator is used outside DEV MODE.")
        }
        logging.warn("Cleaning up resources which were: ${config.resources} to default usable in DEV MODE.")

        return config.copy(resources = config.resources.copy(
                toolboxes = emptySet(),
                modules = emptySet(),
                details = ConfigResourcesDetails(
                        chunks = 1,
                        mem = "1gb",
                        ncpus = 1,
                        walltime = "01:00:00",
                        scratchLocal = "1gb"
                )
        ))
    }

}