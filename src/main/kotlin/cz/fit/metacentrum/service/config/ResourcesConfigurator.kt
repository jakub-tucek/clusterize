package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.domain.config.ConfigFile
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

    override fun configure(config: ConfigFile): ConfigFile {
        val walltime = config.resources.details?.walltime
                ?: consoleReader.askForValue("Please enter wall time [01:00:00]:") { it.ifBlank { "00:04:00" } }

        val chunks = if (config.resources.details?.chunks == null || config.resources.details.chunks < 1)
            consoleReader.askForValue("Please select chunks [1]:") { it.toIntOrNull() ?: 1 }
        else config.resources.details.chunks

        val mem = config.resources.details?.mem
                ?: consoleReader.askForValue("Please enter memory [5gb]:") { it.ifBlank { "5gb" } }

        val ncpus = if (config.resources.details?.ncpus == null || config.resources.details.ncpus < 1)
            consoleReader.askForValue("Please enter number of cpus [8]:") { it.toIntOrNull() ?: 8 }
        else config.resources.details.ncpus

        val scratchLocal = config.resources.details?.scratchLocal
                ?: consoleReader.askForValue("Please enter scratch local space [1gb]:") { it.ifBlank { "1gb" } }

        val ngpus: Int? = if (consoleReader.askForConfirmation("Do you need to specify number of gpu?", false)) {
            config.resources.details?.ngpus
                    ?: consoleReader.askForValue("Please enter number of gpus if needed [1]:") { it.toIntOrNull() ?: 2 }
        } else null
        return config.copy(resources = config.resources.copy(
                details = ConfigResourcesDetails(
                        chunks = chunks,
                        walltime = walltime,
                        mem = mem,
                        ncpus = ncpus,
                        scratchLocal = scratchLocal,
                        ngpus = ngpus
                )
        ))
    }
}
