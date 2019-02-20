package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.CommandOutput
import cz.fit.metacentrum.service.api.ShellService
import mu.KotlinLogging
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger { }
/**
 * Shell wrapper service for running commands.
 * @author Jakub Tucek
 */
class ShellServiceImpl : ShellService {


    /**
     * Returns asynchronously command in bash terminal and returns its pid.
     */
    override fun runCommandAsync(command: String) {
        ProcessBuilder("/bin/sh", "-c", command)
                .start()
    }

    /**
     * Runs commands in console with 5s timeout in /bin/sh. Read text are trimmed.
     */
    override fun runCommand(command: String): CommandOutput {
        val process = ProcessBuilder("/bin/sh", "-c", command)
                .start()

        process.waitFor(30, TimeUnit.SECONDS)
        if (process.isAlive) {
            logger.debug { "Process is still alive. Will hang." }
            System.exit(1)
        }


        val errInput = process.errorStream.bufferedReader().use { it.readText() }.trim()
        val input = process.inputStream.bufferedReader().use { it.readText() }.trim()

        return CommandOutput(
                input, process.exitValue(), errInput
        )
    }

}