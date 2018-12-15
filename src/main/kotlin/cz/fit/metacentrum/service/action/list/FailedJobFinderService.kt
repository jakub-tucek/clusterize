package cz.fit.metacentrum.service.action.list

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJobFailedWrapper
import mu.KotlinLogging
import java.nio.file.Files
import kotlin.streams.toList

private val logger = KotlinLogging.logger {}

/**
 * Service for detecting failed jobs based on exit status.
 * @author Jakub Tucek
 */
class FailedJobFinderService {

    fun findFailedJobs(jobs: List<ExecutionMetadataJob>, runningQueuedPids: List<String>): List<ExecutionMetadataJobFailedWrapper> {
        return jobs
                // filter out running and queued jobs
                .filter { !runningQueuedPids.contains(it.pid) }
                .map { checkForErrorInPath(it) }
                .filterNotNull()

    }

    private fun checkForErrorInPath(job: ExecutionMetadataJob): ExecutionMetadataJobFailedWrapper? {
        val statusFile = job.jobPath.resolve(FileNames.statusLog)
        var status: Int? = null

        if (Files.exists(statusFile)) {
            status = Files.readAllLines(statusFile).first().toIntOrNull()
        } else {
            // status file does not have to exist if job was killed
            logger.info("Status file not found. Job was probably killed")
        }

        // finished Done, no error
        if (status == 0) return null
        val output = Files.list(job.jobPath)
                .filter { it.toString().endsWith(".log") }
                .map {
                    """
                    ==================== ${it.fileName} =========================
                     ${Files.readAllLines(it)}
                     ============================================================
                """.trimIndent()
                }
                .toList()
                .joinToString("\n")

        return ExecutionMetadataJobFailedWrapper(job = job, status = status, output = output)
    }
}