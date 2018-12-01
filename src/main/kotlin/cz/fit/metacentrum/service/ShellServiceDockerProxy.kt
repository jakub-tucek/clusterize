package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.CommandOutput
import cz.fit.metacentrum.service.api.ShellService
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Proxy class for shell service actuall implementation. Prepends command with docker execution.
 * @author Jakub Tucek
 */
class ShellServiceDockerProxy(val shellService: ShellService) : ShellService {
    override fun runCommand(command: String): CommandOutput {
        logger.info("ShellServiceDockerProxy run command wrapper")
        return shellService.runCommand(
                """docker exec -u "pbsuser" pbsmnt bash -c -l "${command}""""
        )
    }

}