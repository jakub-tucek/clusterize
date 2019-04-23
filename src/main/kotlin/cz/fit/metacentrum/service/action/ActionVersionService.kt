package cz.fit.metacentrum.service.action

import cz.fit.metacentrum.domain.ActionVersion
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.input.SerializationService
import javax.inject.Inject

/**
 *
 * @author Jakub Tucek
 */
class ActionVersionService : ActionService<ActionVersion> {

    @Inject
    private lateinit var serializationService: SerializationService

    override fun processAction(argumentAction: ActionVersion) {
        val versionFile = serializationService.readVersionFile()
        println(versionFile?.get("version") ?: "DEVELOPMENT")
    }

}