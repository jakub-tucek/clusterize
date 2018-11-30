package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.CommandOutput
import java.util.concurrent.TimeUnit

/**
 * Shell wrapper service for running commands.
 * @author Jakub Tucek
 */
class ShellService {

    /**
     * Runs commands in console with 5s timeout in /bin/sh. Read text are trimmed.
     */
    fun runCommand(command: String): CommandOutput {
        val process = ProcessBuilder("/bin/sh", "-c", command)
                .start()

        process.waitFor(5, TimeUnit.SECONDS)


        val errInput = process.errorStream.bufferedReader().use { it.readText() }.trim()
        val input = process.inputStream.bufferedReader().use { it.readText() }.trim()

        return CommandOutput(
                input, process.exitValue(), errInput
        )
    }

}