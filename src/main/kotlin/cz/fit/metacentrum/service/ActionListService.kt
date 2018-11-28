package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.ActionList
import cz.fit.metacentrum.domain.ExecutionMetadataComparator
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.util.ConsoleWriter
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject
import kotlin.streams.toList

/**
 *
 * @author Jakub Tucek
 */
class ActionListService() : ActionService<ActionList> {
    @Inject
    private lateinit var serializationService: SerializationService

    override fun processAction(argumentAction: ActionList) {
        val metadataPath = Paths.get(getMetadataPath(argumentAction))

        val metadatas = Files.list(metadataPath)
                .filter { Files.isDirectory(it) }
                .map {
                    val res = serializationService.parseMetadata(it.toString())
                    if (res == null) {
                        ConsoleWriter.writeStatus("Folder ${it.toString()} does not contain metadata file")
                    }
                    res
                }
                .sorted(ExecutionMetadataComparator::compare)
                .toList()
                .filterNotNull()

        ConsoleWriter.writeMetadas(metadatas)
    }

    private fun getMetadataPath(actionList: ActionList): String {
        val (metadataStoragePath, configFile) = actionList
        if (metadataStoragePath != null) {
            return metadataStoragePath
        }
        if (configFile != null) {
            return serializationService.parseConfig(configFile).environment.metadataStoragePath
        }
        throw IllegalArgumentException("Both paths in action list are null.")
    }
}