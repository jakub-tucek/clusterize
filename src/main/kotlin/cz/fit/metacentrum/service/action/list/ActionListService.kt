package cz.fit.metacentrum.service.action.list

import cz.fit.metacentrum.domain.ActionList
import cz.fit.metacentrum.domain.meta.ExecutionMetadataComparator
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.input.SerializationService
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
    @Inject
    private lateinit var checkQueueExecutor: CheckQueueExecutor
    @Inject
    private lateinit var metadataInfoPrinter: MetadataInfoPrinter
    @Inject
    private lateinit var rerunService: TaskResubmitService

    override fun processAction(argumentAction: ActionList) {
        val metadataPath = Paths.get(getMetadataPath(argumentAction))
        if (!Files.exists(metadataPath)) {
            throw IllegalStateException("Path ${metadataPath} does not exists. Exiting.")
        }

        val originalMetadata = Files.list(metadataPath)
                .filter { Files.isDirectory(it) }
                .map {
                    val res = serializationService.parseMetadata(it)
                    if (res == null) {
                        ConsoleWriter.writeStatus("Folder ${it.toString()} does not contain metadata file")
                    }
                    res
                }
                .sorted(ExecutionMetadataComparator::compare)
                .toList()
                .filterNotNull()
        val listedMetadata = originalMetadata.map { checkQueueExecutor.execute(it) }

        // persist info
        listedMetadata
                // persist only changed files
                .filter { !originalMetadata.contains(it) }
                .forEach { serializationService.persistMetadata(it) }

        metadataInfoPrinter.printMetadataListInfo(listedMetadata)
        rerunService.promptRerunIfError(listedMetadata)
    }

    private fun getMetadataPath(actionList: ActionList): String {
        val (metadataStoragePath, configFile) = actionList
        if (metadataStoragePath != null) {
            return metadataStoragePath
        }
        if (configFile != null) {
            return serializationService.parseConfig(configFile).general.metadataStoragePath
        }
        throw IllegalArgumentException("Both paths in action list are null.")
    }
}