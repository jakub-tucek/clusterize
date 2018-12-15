package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.CommandOutput
import cz.fit.metacentrum.service.api.ShellService
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val commandPrefix = "docker exec -u \"pbsuser\" pbsmnt bash -c -l"
/**
 * Proxy class for shell service actual implementation. Prepends command with docker execution.
 * @author Jakub Tucek
 */
class ShellServiceDockerProxy(val shellService: ShellService) : ShellService {
    override fun runCommandAsync(command: String): Long {
        logger.info("ShellServiceDockerProxy run command asynchronously wrapper")
        return shellService.runCommandAsync(
                """$commandPrefix "$command""""
        )
    }

    override fun runCommand(command: String): CommandOutput {
        logger.info("ShellServiceDockerProxy run command wrapper")
        return shellService.runCommand(
                """$commandPrefix "${command}""""
        )
    }

}