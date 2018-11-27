package cz.fit.metacentrum.service.executor

import com.google.inject.Inject
import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.service.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import mu.KotlinLogging
import java.io.IOException

private val logger = KotlinLogging.logger {}

/**
 *
 * @author Jakub Tucek
 */
class SubmitExecutor : TaskExecutor {

    @Inject
    private lateinit var shellService: ShellService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val scripts = metadata.runScripts ?: throw IllegalStateException("No scripts to run available")

        val scriptsWithPid = scripts
                .map {
                    val cmdResult = shellService.runCommand("qsub", it.scriptPath.toAbsolutePath().toString())
                    if (cmdResult.status != 0)
                        throw IOException("Submitting script ${it.scriptPath} failed with ${cmdResult.status}. ${cmdResult.errOutput}")
                    logger.info { "Run ${it.runId} with PID ${cmdResult.output} successfully submitted" }
                    return@map it.copy(pid = cmdResult.output)
                }

        return metadata.copy(runScripts = scriptsWithPid)
    }

}