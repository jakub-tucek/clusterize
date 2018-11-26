package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * @author Jakub Tucek
 */
class InitOutputPathExecutor : TaskExecutor {
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {

        val newName = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        )

        val storagePath = initPath(newName, metadata.storagePath, metadata.configFile.environment.storagePath)
        val metadataStoragePath = initPath(newName, metadata.metadataStoragePath, metadata.configFile.environment.metadataStoragePath)

        return metadata.copy(storagePath = storagePath, metadataStoragePath = metadataStoragePath)
    }

    private fun initPath(folderName: String, pathFromMetadata: Path?, configPath: String): Path {
        val outputPath = when (pathFromMetadata) {
            null -> {
                val basePath = configPath

                val newName = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
                )

                Paths.get(basePath, newName)
            }
            else -> {
                pathFromMetadata
            }
        }

        if (Files.exists(outputPath)) {
            throw IllegalStateException("Output directory $outputPath already exists!")
        } else {
            Files.createDirectories(outputPath)
        }

        return outputPath
    }

}