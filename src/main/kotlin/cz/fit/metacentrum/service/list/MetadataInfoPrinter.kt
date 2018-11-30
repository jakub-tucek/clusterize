package cz.fit.metacentrum.service.list

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateFailed
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateOk
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateRunning
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.defaultCommandDelimiter
import java.time.format.DateTimeFormatter

/**
 *
 * @author Jakub Tucek
 */
class MetadataInfoPrinter {

    fun printMetadataListInfo(metadatas: List<ExecutionMetadata>) {
        ConsoleWriter.writeDelimiter()
        ConsoleWriter.writeHeader("Found tasks:")
        metadatas.forEachIndexed { index, executionMetadata ->
            printMetadataInfo(index, executionMetadata)
        }
        println(defaultCommandDelimiter)
    }


    private fun printMetadataInfo(index: Int, metadata: ExecutionMetadata) {
        val state = metadata.state
        val jobs = metadata.jobs ?: throw IllegalStateException("Jobs not set in metadata")

        val formattedDate = metadata.timestamp.format(
                DateTimeFormatter.ofPattern("dd/MM/YYYY hh:mm")
        )

        val stringBuild = StringBuilder("$index - ${formattedDate}")

        when (state) {
            is ExecutionMetadataStateOk -> {
                stringBuild.append(" - OK")
            }
            is ExecutionMetadataStateFailed -> {
                val failedStateFailed = state
                stringBuild.append(" - ${failedStateFailed.failedJobs.count()}/${jobs.count()} FAILED")
            }
            is ExecutionMetadataStateRunning -> {
                val runningDescription = getRunningDescription(state, jobs.count())
                stringBuild.append(" - RUNNING\n")
                stringBuild.append(runningDescription)
            }
            else -> throw IllegalStateException("Proper state is not set in metadata. ${state}")
        }

        ConsoleWriter.writeStatus(stringBuild.toString())
    }

    private fun getRunningDescription(state: ExecutionMetadataStateRunning, totalCountJobs: Int): String {
        val desc = StringBuilder()
        desc.append(ConsoleWriter.getStatusDetailLine("- ${state.failedJobs.count()}/${totalCountJobs} FAILED"))
        desc.append("\n")
        desc.append(ConsoleWriter.getStatusDetailLine("- ${state.queuedJobs.count()}/${totalCountJobs} QUEUED"))
        desc.append("\n")
        desc.append(ConsoleWriter.getStatusDetailLine("- ${state.runningJobs.count()}/${totalCountJobs} RUNNING"))
        desc.append("\n")
        val runningJobsDetail = state.runningJobs
                .map { "${it.job.pid}: ${it.runTime}" }
                .map { ConsoleWriter.getStatusDetailLine("   $it") }
                .joinToString("\n")
        desc.append(runningJobsDetail)

        return desc.toString()
    }

}