package cz.fit.metacentrum.service.action.cron

import cz.fit.metacentrum.domain.ActionCronStartInternal
import cz.fit.metacentrum.service.api.ActionService
import javax.inject.Inject

/**
 *
 * @author Jakub Tucek
 */
class ActionCronStartInternalService : ActionService<ActionCronStartInternal> {

    @Inject
    private lateinit var watcherService: WatcherService

    override fun processAction(argumentAction: ActionCronStartInternal) {
        watcherService.checkMetadataStatus()
    }

}