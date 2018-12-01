package cz.fit.metacentrum.service.api

import cz.fit.metacentrum.domain.CommandOutput

/**
 *
 * @author Jakub Tucek
 */
interface ShellService {
    fun runCommand(command: String): CommandOutput
}