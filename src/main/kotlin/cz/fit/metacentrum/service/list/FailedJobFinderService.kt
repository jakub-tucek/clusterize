package cz.fit.metacentrum.service.list

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

    fun findFailedJobs(jobs: List<ExecutionMetadataJob>): List<ExecutionMetadataJobFailedWrapper> {
        return jobs
                // if status is missing it should mean that job is not finished
                .filter { Files.exists(it.jobPath.resolve(FileNames.statusLog)) }
                .map { checkForErrorInPath(it) }
                .filterNotNull()

    }

    private fun checkForErrorInPath(job: ExecutionMetadataJob): ExecutionMetadataJobFailedWrapper? {
        val statusFile = job.jobPath.resolve(FileNames.statusLog)
        var status: Int? = null

        try {
            status = Files.readAllLines(statusFile).first().toInt()
        } catch (e: NumberFormatException) {
            logger.error("Status file cannot be read")
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