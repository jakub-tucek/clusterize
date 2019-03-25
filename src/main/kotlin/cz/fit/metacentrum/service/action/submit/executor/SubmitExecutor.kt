package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.api.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.QueueUtils
import java.io.IOException
import javax.inject.Inject

/**
 * Submits created job to queue and persists pid.
 * @author Jakub Tucek
 */
class SubmitExecutor : TaskExecutor {

    @Inject
    private lateinit var shellService: ShellService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Submitting runs/jobs to queue")
        val jobs = metadata.jobs ?: throw IllegalStateException("No scripts to run available")

        var messageBuffer = StringBuilder()
        val step = 20;

        val scriptsWithPid = jobs
                .mapIndexed { index, it ->
                    if (it.jobInfo.state != ExecutionMetadataState.INITIAL) {
                        return@mapIndexed it
                    }
                    val scriptFile = it.jobPath.resolve(FileNames.innerScript).toAbsolutePath()
                    val cmdResult = shellService.runCommand("qsub $scriptFile")
                    if (cmdResult.status != 0)
                        throw IOException("Submitting script $scriptFile failed with ${cmdResult.status}. ${cmdResult.errOutput}")


                    messageBuffer.append("Run ${it.jobId} submitted under ${cmdResult.output}\n")
                    if (index % step == 0) {
                        println(messageBuffer)
                        messageBuffer = StringBuilder()
                    }

                    it.copy(jobInfo = it.jobInfo.copy(pid = QueueUtils.extractPid(cmdResult.output), state = ExecutionMetadataState.QUEUED))
                }
        println(messageBuffer)

        return metadata.copy(jobs = scriptsWithPid)
    }

}