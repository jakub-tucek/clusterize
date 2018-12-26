package cz.fit.metacentrum.service.action.submit.executor.re

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.api.TaskExecutor
import java.nio.file.Files

/**
 * Cleans jobs directories and prepares task for rerun of only failed jobs.
 * @author Jakub Tucek
 */
class CleanEmptyStateFoldersExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val jobs = metadata.jobs ?: throw IllegalStateException("Jobs are missing in metedata")
        val updatedJobs = jobs.map {
            if (it.jobInfo.state != ExecutionMetadataState.INITIAL) {
                it
            } else {
                cleanDir(it, metadata.jobsHistory.size)
            }
        }
        return metadata.copy(jobs = updatedJobs, currentState = ExecutionMetadataState.INITIAL)
    }

    private fun cleanDir(job: ExecutionMetadataJob, rerunId: Int): ExecutionMetadataJob {
        // get job folder name
        val currentFolderName = job.jobPath.fileName.toString()
        // create new job folder path by getting parent of job and current name appended with RERUN_{ID}
        val newJobPath = job.jobPath.parent.resolve("${currentFolderName}_RERUN_$rerunId")

        Files.createDirectories(newJobPath)
        Files.copy(job.jobPath.resolve(FileNames.innerScript), newJobPath.resolve(FileNames.innerScript))
        return job.copy(
                jobPath = newJobPath
        )
    }
}