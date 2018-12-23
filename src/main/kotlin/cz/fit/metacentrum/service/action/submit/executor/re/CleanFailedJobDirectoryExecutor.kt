package cz.fit.metacentrum.service.action.submit.executor.re

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.FileUtils
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

/**
 * Cleans failed jobs directories and prepares task for rerun of only failed jobs.
 * @author Jakub Tucek
 */
class CleanFailedJobDirectoryExecutor : TaskExecutor {

    // TODO: Add cleanup
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Cleaning up storage paths of failed jobs.")
//        val stateFailed = metadata.state as ExecutionMetadataStateFailed
//        stateFailed.failedJobs.forEach { cleanStorageJobPath(it.job.jobPath) }

        return metadata
    }

    /**
     * Deletes all files from job directory except inner script
     */
    private fun cleanStorageJobPath(it: Path) {
        val scriptPath = it.resolve(FileNames.innerScript)
        val script = Files.readAllBytes(it.resolve(FileNames.innerScript))
        FileUtils.deleteFolder(it)
        Files.createDirectory(it)
        Files.write(scriptPath, script, StandardOpenOption.CREATE_NEW)
        ConsoleWriter.writeStatusDetail("Folder ${it.toString()} cleaned up")
    }

}