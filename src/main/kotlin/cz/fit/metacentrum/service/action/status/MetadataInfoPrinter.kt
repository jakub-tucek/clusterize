package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.config.userDateFormat
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateDone
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateFailed
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateRunning
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.defaultCommandDelimiter
import java.time.format.DateTimeFormatter

/**
 * Printer of collected metadata information. Prints state of found tasks.
 * @author Jakub Tucek
 */
class MetadataInfoPrinter {

    fun printMetadataListInfo(metadatas: List<ExecutionMetadata>) {
        ConsoleWriter.writeDelimiter()
        ConsoleWriter.writeHeader("Found tasks:")
        metadatas.forEachIndexed { index, executionMetadata ->
            ConsoleWriter.writeListItem(getMetadataInfo(index, executionMetadata))
        }
        println(defaultCommandDelimiter)
    }

    fun getMetadataInfo(index: Int, metadata: ExecutionMetadata): String {
        val state = metadata.state
        val jobs = metadata.jobs ?: throw IllegalStateException("Jobs not set in metadata")

        val formattedDate = metadata.creationTime.format(DateTimeFormatter.ofPattern(userDateFormat))

        val stringBuild = StringBuilder("$index - ${metadata.configFile.general.taskName} - ${formattedDate}")

        when (state) {
            is ExecutionMetadataStateDone -> {
                stringBuild.append(" - DONE")
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
        return stringBuild.toString()
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