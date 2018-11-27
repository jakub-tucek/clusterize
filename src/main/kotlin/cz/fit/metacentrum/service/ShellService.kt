package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.CommandOutput
import java.util.concurrent.TimeUnit

/**
 * Shell wrapper service for running commands.
 * @author Jakub Tucek
 */
class ShellService {

    /**
     * Runs commands in console with 5s timeout. Commands that would normally be split by spaces like:
     * 'echo 123' must be passed as two commands: 'echo', '123'.
     */
    fun runCommand(vararg commands: String): CommandOutput {
        val process = ProcessBuilder(*commands)
                .start()

        process.waitFor(5, TimeUnit.SECONDS)


        val errInput = process.errorStream.bufferedReader().use { it.readText() }
        val input = process.inputStream.bufferedReader().use { it.readText() }

        return CommandOutput(
                input, process.exitValue(), errInput
        )
    }

}