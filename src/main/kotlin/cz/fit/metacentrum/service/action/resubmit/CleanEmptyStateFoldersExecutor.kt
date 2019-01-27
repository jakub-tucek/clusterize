package cz.fit.metacentrum.service.action.resubmit

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import java.nio.file.Files

/**
 * Cleans jobs directories and prepares task for rerun of only failed jobs.
 * @author Jakub Tucek
 */
class CleanEmptyStateFoldersExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Preparing jobs for resubmit")

        val jobs = metadata.jobs ?: throw IllegalStateException("Jobs are missing in metadata")
        val updatedJobs = jobs.map {
            if (it.jobInfo.state != ExecutionMetadataState.INITIAL) {
                it
            } else {
                ConsoleWriter.writeStatusDetail("Preparing directory for job ${it.jobId}")
                cleanDir(it)
            }
        }
        return metadata.copy(jobs = updatedJobs, currentState = ExecutionMetadataState.INITIAL)
    }

    private fun cleanDir(job: ExecutionMetadataJob): ExecutionMetadataJob {
        // get job folder name without rerun postfix
        val baseFolderName = job.jobPath.fileName.toString()
                .replace("""_RERUN_[0-9]+""".toRegex(), "")
        // create new job folder path by getting parent of job and current name appended with RERUN_{ID}
        val newJobPath = job.jobPath.parent.resolve("${baseFolderName}_RERUN_${job.resubmitCounter}")

        Files.createDirectories(newJobPath)
        Files.copy(job.jobPath.resolve(FileNames.innerScript), newJobPath.resolve(FileNames.innerScript))
        return job.copy(
                jobPath = newJobPath
        )
    }
}