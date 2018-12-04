package cz.fit.metacentrum.service.submit.executor

import com.google.inject.Inject
import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.QueueUtils
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
                    val scriptFile = it.jobPath.resolve(FileNames.innerScript).toAbsolutePath()
                    val cmdResult = shellService.runCommand("qsub ${scriptFile.toString()}")
                    if (cmdResult.status != 0)
                        throw IOException("Submitting script ${scriptFile} failed with ${cmdResult.status}. ${cmdResult.errOutput}")

                    ConsoleWriter.writeStatusDetail("Run ${it.jobId} submitted under ${cmdResult.output}")
                    it.copy(pid = QueueUtils.extractPid(cmdResult.output))
                }

        return metadata.copy(jobs = scriptsWithPid)
    }

}