package cz.fit.metacentrum.service.executor.submit

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.format.DateTimeFormatter

/**
 *
 * @author Jakub Tucek
 */
class InitOutputPathExecutor : TaskExecutor {
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val newName = metadata.timestamp.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        )
        ConsoleWriter.writeStatus("Initializing output directories under name $newName")

        val storagePath = initPath(newName, metadata.storagePath, metadata.configFile.environment.storagePath)
        val metadataStoragePath = initPath(newName, metadata.metadataStoragePath, metadata.configFile.environment.metadataStoragePath)

        return metadata.copy(storagePath = storagePath, metadataStoragePath = metadataStoragePath)
    }

    private fun initPath(folderName: String, pathFromMetadata: Path?, configPath: String): Path {
        val outputPath = when (pathFromMetadata) {
            null -> Paths.get(configPath, folderName)
            else -> pathFromMetadata
        }

        if (Files.exists(outputPath)) {
            throw IllegalStateException("Output directory $outputPath already exists!")
        } else {
            Files.createDirectories(outputPath)
        }

        return outputPath
    }

}