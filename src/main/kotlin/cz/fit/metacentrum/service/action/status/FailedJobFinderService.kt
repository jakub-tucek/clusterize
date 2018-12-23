package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
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

    fun updateJobState(jobs: List<ExecutionMetadataJob>, pidMap: Map<String, List<QueueRecord>>): List<ExecutionMetadataJob> {
        return jobs
                .map {
                    val record = pidMap.get(it.jobInfo.pid)?.first()
                    if (record != null) {
                        when (record.state) {
                            QueueRecord.State.QUEUED -> return@map it
                            QueueRecord.State.RUNNING -> return@map it
                        }
                    }

                    val jobState = checkForErrorInPath(it)
                    return@map jobState
                }
    }

    private fun checkForErrorInPath(job: ExecutionMetadataJob): ExecutionMetadataJob {
        val status: Int? = job.jobInfo.status

        if (status == null) {
            logger.info("Status file not found. Job was probably killed")
        }
        // finished Done, no error
        if (status == 0) return job
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

        return job.copy(jobInfo = job.jobInfo.copy(state = ExecutionMetadataState.FAILED, output = output))
    }
}