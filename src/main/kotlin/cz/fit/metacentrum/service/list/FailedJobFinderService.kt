package cz.fit.metacentrum.service.list

import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJobFailedWrapper
import mu.KotlinLogging
import java.nio.file.Files
import kotlin.streams.toList

private val logger = KotlinLogging.logger {}

/**
 *
 * @author Jakub Tucek
 */
class FailedJobFinderService {

    fun findFailedJobs(jobs: List<ExecutionMetadataJob>): List<ExecutionMetadataJobFailedWrapper> {
        return jobs
                // if status is missing it should mean that job is not finished
                .filter { Files.exists(it.runPath.resolve("status.log")) }
                .map { checkForErrorInPath(it) }
                .filterNotNull()

    }

    private fun checkForErrorInPath(job: ExecutionMetadataJob): ExecutionMetadataJobFailedWrapper? {
        val statusFile = job.runPath.resolve("status.log")
        var status: Int? = null
        try {
            status = Files.readAllLines(statusFile).first().toInt()
        } catch (e: NumberFormatException) {
            logger.error("Status file is is missing ")
        }
        // finished OK, no error
        if (status == 0) return null
        val output = Files.list(job.runPath)
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