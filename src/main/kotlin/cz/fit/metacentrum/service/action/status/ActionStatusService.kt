package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.domain.ActionStatus
import cz.fit.metacentrum.service.action.cron.CronService
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.input.SerializationService
import java.nio.file.Files
import java.nio.file.Paths
import javax.inject.Inject

/**
 * ActionStatusService for status action.
 * @author Jakub Tucek
 */
class ActionStatusService : ActionService<ActionStatus> {

    @Inject
    private lateinit var serializationService: SerializationService
    @Inject
    private lateinit var metadataStatusService: MetadataStatusService
    @Inject
    private lateinit var metadataInfoPrinter: MetadataInfoPrinter
    @Inject
    private lateinit var cronService: CronService

    override fun processAction(argumentAction: ActionStatus) {
        val metadataPath = Paths.get(getMetadataPath(argumentAction))
        if (!Files.exists(metadataPath)) {
            throw IllegalStateException("Path ${metadataPath} does not exists. Exiting.")
        }

        val originalMetadata = metadataStatusService.retrieveMetadata(metadataPath)

        val updatedMetadata = originalMetadata
                .map(metadataStatusService::updateMetadataState)
        metadataInfoPrinter.printMetadataListInfo(updatedMetadata)

        updatedMetadata
                .filter { metadataStatusService.isUpdatedMetadata(originalMetadata, it) }
                .forEach {
                    // save file if cron is not registered to avoid race condition when saving files
                    if (!cronService.isRegistered()) {
                        serializationService.persistMetadata(it)
                    }
                }
    }

    private fun getMetadataPath(actionStatus: ActionStatus): String {
        val (metadataStoragePath, configFile) = actionStatus
        if (metadataStoragePath != null) {
            return metadataStoragePath
        }
        if (configFile != null) {
            return serializationService.parseConfig(configFile).general.metadataStoragePath
        }
        throw IllegalArgumentException("Both paths in action list are null.")
    }
}