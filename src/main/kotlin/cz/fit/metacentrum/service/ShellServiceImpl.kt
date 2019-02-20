package cz.fit.metacentrum.service

import cz.fit.metacentrum.config.appName
import cz.fit.metacentrum.domain.CommandOutput
import cz.fit.metacentrum.service.api.ShellService
import mu.KotlinLogging
import java.nio.file.Files
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
        val out = Files.createTempFile(appName, "command-out")
        val outErr = Files.createTempFile(appName, "command-out")
        Files.deleteIfExists(out)
        Files.deleteIfExists(outErr)
        Files.createFile(out)
        Files.createFile(outErr)

        val process = ProcessBuilder("/bin/sh", "-c", command)
                .redirectError(outErr.toFile())
                .redirectOutput(out.toFile())
                .start()

        process.waitFor(60, TimeUnit.SECONDS)
        if (process.isAlive) {
            logger.debug { "Process did not end. Finishing" }
            return CommandOutput("", 146, "")
        }

        val commandOutput = CommandOutput(
                Files.readAllBytes(out).toString(),
                process.exitValue(),
                Files.readAllBytes(outErr).toString()
        )
        Files.delete(out)
        Files.delete(outErr)
        return commandOutput
    }

}