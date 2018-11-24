package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * @author Jakub Tucek
 */
class InitOutputPathExecutor : TaskExecutor {
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {


        val outputPath = when (metadata.executionOutputPath) {
            null -> {
                val basePath = metadata.configFile.environment.basePath

                val newName = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
                )

                Paths.get(basePath, newName)
            }
            else -> {
                metadata.executionOutputPath
            }
        }

        if (Files.exists(outputPath)) {
            throw IllegalStateException("Output directory $outputPath already exists!")
        } else {
            Files.createDirectories(outputPath)
        }

        return metadata.copy(executionOutputPath = outputPath)
    }

}