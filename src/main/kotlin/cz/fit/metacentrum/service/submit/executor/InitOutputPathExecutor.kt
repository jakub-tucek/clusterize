package cz.fit.metacentrum.service.submit.executor

import cz.fit.metacentrum.domain.config.ConfigEnvironment
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

/**
 *
 * @author Jakub Tucek
 */
class InitOutputPathExecutor : TaskExecutor {
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val formattedTimestamp = metadata.timestamp.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        )
        val newName = getNewName(metadata.configFile.environment, formattedTimestamp)

        ConsoleWriter.writeStatus("Initializing output directories under name $formattedTimestamp")

        val storagePath = initPath(newName, metadata.storagePath, metadata.configFile.environment.storagePath)
        val metadataStoragePath = initPath(newName, metadata.metadataStoragePath, metadata.configFile.environment.metadataStoragePath)

        return metadata.copy(storagePath = storagePath, metadataStoragePath = metadataStoragePath)
    }

    /**
     * Checks storage path and finds last task folder name. Parses order from folder name, add 1 and uses it as new name.
     */
    private fun getNewName(environment: ConfigEnvironment, formattedTimestamp: String): String {
        val storagePath = Paths.get(environment.storagePath)

        // TODO: Move to constant
        val taskFolderPrefix = "task-"
        val taskFolderRegex = """^task-([0-9]+).*$""".toRegex()
        // default new name
        var newName = "${taskFolderPrefix}1"
        if (Files.exists(storagePath)) {
            val highestFolderNumber: Int? = Files.list(storagePath)
                    .filter { Files.isDirectory(it) && it.fileName.toString().matches(taskFolderRegex) }
                    .map {
                        val (order) = taskFolderRegex.find(it.fileName.toString())!!.destructured
                        order.toInt()
                    }
                    .toList()
                    .filterNotNull()
                    .sortedDescending()
                    .firstOrNull()
            if (highestFolderNumber != null) {
                newName = "${taskFolderPrefix}_${highestFolderNumber + 1}"
            }
        }

        return "$newName-$formattedTimestamp"
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