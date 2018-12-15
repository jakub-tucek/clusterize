package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.FileUtils
import java.nio.file.Paths

/**
 * Copy sources to metadata directory
 * @author Jakub Tucek
 */
class CopySourcesFilesExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Copying sources files from ${metadata.configFile.general.sourcesPath}")

        val outPath = metadata.paths.metadataStoragePath ?: throw IllegalStateException("MetadataStorage path not set")
        val sourcesOutPath = outPath.resolve(FileNames.sourcesFolder)

        FileUtils.copyDirectory(
                Paths.get(metadata.configFile.general.sourcesPath),
                outPath.resolve(FileNames.sourcesFolder)
        )

        return metadata.copy(paths = metadata.paths.copy(sourcesPath = sourcesOutPath))
    }

}