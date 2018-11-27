package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.ActionList
import cz.fit.metacentrum.service.api.ActionService
import javax.inject.Inject

/**
 *
 * @author Jakub Tucek
 */
class ActionListService() : ActionService<ActionList> {
    @Inject
    private lateinit var serializationService: SerializationService

    override fun processAction(argumentAction: ActionList) {
        val config = getMetadataPath(argumentAction)

        println(config)
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