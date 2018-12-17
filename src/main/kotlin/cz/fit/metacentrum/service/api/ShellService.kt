package cz.fit.metacentrum.service.api

import cz.fit.metacentrum.domain.CommandOutput

/**
 * Shell service is service that is responsible for running given command in console.
 * @author Jakub Tucek
 */
interface ShellService {
    fun runCommand(command: String): CommandOutput
    fun runCommandAsync(command: String)
}