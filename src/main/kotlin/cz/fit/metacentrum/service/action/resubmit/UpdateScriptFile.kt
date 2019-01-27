package cz.fit.metacentrum.service.action.resubmit

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.api.TaskExecutor
import java.nio.file.Files
import java.nio.file.StandardOpenOption

/**
 *
 * @author Jakub Tucek
 */
class UpdateScriptFile : TaskExecutor {


    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        (metadata.jobs ?: throw IllegalStateException("Jobs missing"))
                .filter { it.jobInfo.state == ExecutionMetadataState.INITIAL }
                .forEach {

                    val originalPath = (it.jobParent ?: throw IllegalStateException("Job parent missing in resubmit"))
                            .jobPath.toAbsolutePath().toString()

                    val originalScript = it.jobPath.resolve(FileNames.innerScript)
                    val newPath = it.jobPath.toAbsolutePath().toString()

                    val newContent = Files.readAllLines(originalScript)
                            .map { line -> line.replace(originalPath, newPath) }

                    Files.write(originalScript, newContent, StandardOpenOption.TRUNCATE_EXISTING)
                }
        return metadata
    }

}