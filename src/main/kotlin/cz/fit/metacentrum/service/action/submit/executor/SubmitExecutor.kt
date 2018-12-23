package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.QueueUtils
import mu.KotlinLogging
import java.io.IOException
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

/**
 * Submits created job to queue and persists pid.
 * @author Jakub Tucek
 */
class SubmitExecutor : TaskExecutor {

    @Inject
    private lateinit var shellService: ShellService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Submitting runs/jobs to queue")
        val allJobs = metadata.jobs ?: throw IllegalStateException("No scripts to run available")

        val scriptsWithPid = allJobs
                .map {
                    val scriptFile = it.jobPath.resolve(FileNames.innerScript).toAbsolutePath()
                    val cmdResult = shellService.runCommand("qsub ${scriptFile.toString()}")
                    if (cmdResult.status != 0)
                        throw IOException("Submitting script ${scriptFile} failed with ${cmdResult.status}. ${cmdResult.errOutput}")

                    ConsoleWriter.writeStatusDetail("Run ${it.jobId} submitted under ${cmdResult.output}")
                    it.copy(jobInfo = it.jobInfo.copy(pid = QueueUtils.extractPid(cmdResult.output)))
                }

        return metadata.copy(jobs = scriptsWithPid)
    }

}