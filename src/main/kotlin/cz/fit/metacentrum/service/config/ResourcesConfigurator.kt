package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.ConfigResourceProfile
import cz.fit.metacentrum.domain.config.ConfigResourcesDetails
import cz.fit.metacentrum.service.ConsoleReader
import cz.fit.metacentrum.service.api.Configurator
import javax.inject.Inject

/**
 * Configurator for base resources
 * @author Jakub Tucek
 */
class ResourcesConfigurator : Configurator {

    @Inject
    private lateinit var consoleReader: ConsoleReader

    override fun configureInteractively(config: ConfigFile): ConfigFile {
        if (config.resources.details == null
                && config.resources.profile == ConfigResourceProfile.CUSTOM) {
            val walltime = consoleReader.askForValue("Please enter wall time [01:00:00]:") { it.ifBlank { "00:04:00" } }
            val chunks = consoleReader.askForValue("Please chunks [1]:") { it.toIntOrNull() ?: 1 }
            val mem = consoleReader.askForValue("Please enter memory [5gb]:") { it.ifBlank { "5gb" } }
            val ncpus = consoleReader.askForValue("Please enter number of cpus [8]:") { it.toIntOrNull() ?: 8 }
            val scratchLocal = consoleReader.askForValue("Please enter scratch local space [1gb]:") { it.ifBlank { "1gb" } }

            return config.copy(resources = config.resources.copy(
                    details = ConfigResourcesDetails(
                            chunks = chunks,
                            walltime = walltime,
                            mem = mem,
                            ncpus = ncpus,
                            scratchLocal = scratchLocal
                    )
            ))
        }
        return config
    }
}