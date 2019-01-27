package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.config.userDateFormat
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
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
        metadatas.forEachIndexed(this::printMetadataInfo)
        println(defaultCommandDelimiter)
    }

    fun printMetadataInfo(index: Int, metadata: ExecutionMetadata) {
        ConsoleWriter.writeListItem(getMetadataInfo(index + 1, metadata))
    }

    fun getMetadataInfo(index: Int, metadata: ExecutionMetadata): String {
        val state = metadata.currentState
        val jobs = metadata.jobs ?: throw IllegalStateException("Jobs not set in metadata")

        val formattedDate = metadata.creationTime.format(DateTimeFormatter.ofPattern(userDateFormat))

        val stringBuild = StringBuilder("$index - ${metadata.configFile.general.taskName} - ${formattedDate}")

        val failedJobs = metadata.getJobsByState(ExecutionMetadataState.FAILED)
        when (state) {
            ExecutionMetadataState.DONE -> {
                stringBuild.append(" - DONE")
            }
            ExecutionMetadataState.FAILED -> {
                stringBuild.append(" - ${failedJobs.count()}/${jobs.count()} FAILED")
            }
            ExecutionMetadataState.RUNNING -> {
                val runningDescription = getRunningDescription(metadata, failedJobs)
                stringBuild.append(" - RUNNING\n")
                stringBuild.append(runningDescription)
            }
            ExecutionMetadataState.QUEUED -> {
                stringBuild.append(" - QUEUED")
            }
            else -> throw AssertionError("Unexpected metadata state. $state")
        }
        return stringBuild.toString()
    }


    private fun getRunningDescription(metadata: ExecutionMetadata, failedJobs: List<ExecutionMetadataJob>): String {
        val totalCountJobs = metadata.jobs!!.count()
        val runningJobs = metadata.getJobsByState(ExecutionMetadataState.RUNNING)
        val desc = StringBuilder()
        desc.append(ConsoleWriter.getStatusDetailLine(
                "- ${metadata.getJobsByState(ExecutionMetadataState.DONE).count()}/${totalCountJobs} DONE"))
        desc.append("\n")
        desc.append(ConsoleWriter.getStatusDetailLine(
                "- ${failedJobs.count()}/${totalCountJobs} FAILED"))
        desc.append("\n")
        desc.append(ConsoleWriter.getStatusDetailLine(
                "- ${metadata.getJobsByState(ExecutionMetadataState.QUEUED).count()}/${totalCountJobs} QUEUED"))
        desc.append("\n")
        desc.append(ConsoleWriter.getStatusDetailLine(
                "- ${metadata.getJobsByState(ExecutionMetadataState.RUNNING).count()}/${totalCountJobs} RUNNING"))
        desc.append("\n")
        val runningJobsDetail = runningJobs
                .map { "${it.jobInfo.pid}: ${it.jobInfo.runningTime}" }
                .map { ConsoleWriter.getStatusDetailLine("   $it") }
                .joinToString("\n")
        desc.append(runningJobsDetail)

        return desc.toString()
    }

    fun printMetadataHistory(metadata: ExecutionMetadata): String {
        val res = StringBuilder()

        res.append("\n===== Job history =====\n")
        res.append("\n Total resubmits count: ${metadata.totalResubmits}\n\n")
        metadata.jobs!!.forEachIndexed { index, job ->
            res.append("\n")
            res.append(index)
            res.append(") \n")
            res.append(" - ${job.jobPath.subpath(job.jobPath.nameCount - 2, job.jobPath.nameCount)}")
            res.append("    - ${job.jobInfo.state} - ${job.jobInfo.start}/${job.jobInfo.end}\n")
            res.append("    - ${job.jobInfo.start}/${job.jobInfo.end}\n")
            res.append("    - ${job.resubmitCounter}\n")
            res.append("\n")
        }
        return res.toString()
    }

}