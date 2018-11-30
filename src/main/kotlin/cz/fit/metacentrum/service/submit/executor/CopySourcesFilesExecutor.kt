package cz.fit.metacentrum.service.submit.executor

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.FileUtils
import java.nio.file.Paths

/**
 *
 * @author Jakub Tucek
 */
class CopySourcesFilesExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Copying sources files from ${metadata.configFile.environment.sourcesPath}")

        val outPath = metadata.metadataStoragePath ?: throw IllegalStateException("MetadataStorage path not set")
        val sourcesOutPath = outPath.resolve("sources")

        FileUtils.copyDirectory(
                Paths.get(metadata.configFile.environment.sourcesPath),
                outPath.resolve("sources")
        )

        return metadata.copy(sourcesPath = sourcesOutPath)
    }

}