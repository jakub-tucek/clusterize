package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.config.ConfigGeneral
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

/**
 * Initializes output paths and initializes metadata id.
 *
 * @author Jakub Tucek
 */
class InitOutputPathExecutor : TaskExecutor {
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val formattedTimestamp = metadata.creationTime.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
        )
        val (folderNumber, folderName) = getNewName(metadata.configFile.general, formattedTimestamp)

        ConsoleWriter.writeStatus("Initializing output directories under name $formattedTimestamp")

        val storagePath = initPath(folderName, metadata.paths.storagePath, metadata.configFile.general.storagePath)
        val metadataStoragePath = initPath(folderName, metadata.paths.metadataStoragePath, metadata.configFile.general.metadataStoragePath)

        return metadata.copy(
                paths = metadata.paths.copy(storagePath = storagePath, metadataStoragePath = metadataStoragePath),
                metadataId = folderNumber
        )
    }

    /**
     * Checks storage path and finds last task folder name. Parses order from folder name, add 1 and uses it as new name.
     */
    private fun getNewName(general: ConfigGeneral, formattedTimestamp: String): NewName {
        val storagePath = Paths.get(general.metadataStoragePath)

        val taskFolderPrefix = FileNames.storageTaskFolderPrefix
        val taskFolderRegex = FileNames.storageTaskFolderRegex
        // default new name
        var folderNumber = 1
        if (Files.exists(storagePath)) {
            val highestFolderNumber: Int? = Files.list(storagePath)
                    .filter { Files.isDirectory(it) && it.fileName.toString().matches(taskFolderRegex) }
                    .map {
                        val (order) = taskFolderRegex.find(it.fileName.toString())!!.destructured
                        order.toInt()
                    }
                    .use { it.toList().filterNotNull().max() }
            if (highestFolderNumber != null) {
                folderNumber = highestFolderNumber + 1
            }
        }

        return NewName(folderNumber, "${taskFolderPrefix}${folderNumber}__$formattedTimestamp")
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


    private data class NewName(
            val folderNumber: Int,
            val folderName: String
    )

}