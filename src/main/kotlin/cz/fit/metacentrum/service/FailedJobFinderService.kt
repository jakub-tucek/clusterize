package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJobFailedWrapper
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

private val logger = KotlinLogging.logger {}

/**
 *
 * @author Jakub Tucek
 */
class FailedJobFinderService {

    fun findFailedJobs(storagePath: Path, jobs: List<ExecutionMetadataJob>): List<ExecutionMetadataJobFailedWrapper> {
        return Files.list(storagePath)
                .toList()
                // if status is missing it should mean that job is not finished
                .filter { Files.exists(it.resolve("status.log")) }
                .mapIndexed { index, path -> checkForErrorInPath(path, jobs[index]) }
                .filterNotNull()

    }

    private fun checkForErrorInPath(jobPath: Path, executionMetadataJob: ExecutionMetadataJob): ExecutionMetadataJobFailedWrapper? {
        val statusFile = jobPath.resolve("status.log")
        var status: Int? = null
        try {
            status = Files.readAllLines(statusFile).first().toInt()
        } catch (e: NumberFormatException) {
            logger.error("Status file is is missing ")
        }
        if (status == 1) return null
        val output = Files.walk(jobPath)
                .filter { it.fileName.endsWith(".log") }
                .map {
                    """
                    ==================== ${it.fileName} =========================
                     ${Files.readAllLines(it)}
                     ============================================================
                """.trimIndent()
                }
                .toList()
                .joinToString("\n")

        return ExecutionMetadataJobFailedWrapper(job = executionMetadataJob, status = status, output = output)
    }
}