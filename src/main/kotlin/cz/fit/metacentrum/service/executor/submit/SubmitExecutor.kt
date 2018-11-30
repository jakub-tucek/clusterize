package cz.fit.metacentrum.service.executor.submit

import com.google.inject.Inject
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
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
        ConsoleWriter.writeStatus("Submitting runs/jobs to queue")
        val scripts = metadata.jobs ?: throw IllegalStateException("No scripts to run available")

        val scriptsWithPid = scripts
                .map {
                    val cmdResult = shellService.runCommand("qsub", it.runPath.toAbsolutePath().toString())
                    if (cmdResult.status != 0)
                        throw IOException("Submitting script ${it.runPath} failed with ${cmdResult.status}. ${cmdResult.errOutput}")

                    ConsoleWriter.writeStatusDetail("Run ${it.runId} submitted under ${cmdResult.output.replace("\n", "")}")
                    it.copy(pid = cmdResult.output)
                }

        return metadata.copy(jobs = scriptsWithPid)
    }

}