package cz.fit.metacentrum.service.action.analyze

import cz.fit.metacentrum.domain.ActionAnalyze
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.input.SerializationService
import java.nio.file.Paths
import javax.inject.Inject

/**
 */
class ActionAnalyzeService : ActionService<ActionAnalyze> {

    @Inject
    private lateinit var serializationService: SerializationService

    override fun processAction(argumentAction: ActionAnalyze) {
        val clusterDetails = serializationService.parseClusterDetails(Paths.get(argumentAction.cluterDetailsFile))
        println(clusterDetails)

    }


}