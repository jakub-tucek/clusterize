package cz.fit.metacentrum.util

import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import java.nio.file.Files
import kotlin.streams.toList

/**
 *
 * @author Jakub Tucek
 */
object JobUtils {
    fun updateAsFailedJob(job: ExecutionMetadataJob): ExecutionMetadataJob {
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