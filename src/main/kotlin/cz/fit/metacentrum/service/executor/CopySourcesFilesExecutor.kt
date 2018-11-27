package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.FileUtils
import java.nio.file.Paths

/**
 *
 * @author Jakub Tucek
 */
class CopySourcesFilesExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val matlabAction = metadata.configFile.taskType as MatlabTaskType

        val outPath = metadata.metadataStoragePath ?: throw IllegalStateException("MetadataStorage path not set")
        val sourcesOutPath = outPath.resolve("sources")

        FileUtils.copyDirectory(
                Paths.get(metadata.configFile.environment.sourcesPath),
                outPath.resolve("sources")
        )

        return metadata.copy(sourcesPath = sourcesOutPath)
    }

}