package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJobFailedWrapper
import cz.fit.metacentrum.service.input.SerializationService
import mu.KotlinLogging
import java.nio.file.Files
import javax.inject.Inject
import kotlin.streams.toList

private val logger = KotlinLogging.logger {}

/**
 * Service for detecting failed jobs based on exit status.
 * @author Jakub Tucek
 */
class FailedJobFinderService {


    @Inject
    private lateinit var serializationService: SerializationService

    fun findFailedJobs(jobs: List<ExecutionMetadataJob>, runningQueuedPids: List<String>): List<ExecutionMetadataJobFailedWrapper> {
        return jobs
                // filter out running and queued jobs
                .filter { !runningQueuedPids.contains(it.jobInfo.pid) }
                .map { checkForErrorInPath(it) }
                .filterNotNull()

    }

    private fun checkForErrorInPath(job: ExecutionMetadataJob): ExecutionMetadataJobFailedWrapper? {
        val status: Int? = job.jobInfo.status

        if (status == null) {
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

        return ExecutionMetadataJobFailedWrapper(job = job, output = output)
    }
}