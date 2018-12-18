package cz.fit.metacentrum.service.action.cron

import cz.fit.metacentrum.domain.ActionCron
import cz.fit.metacentrum.service.ShellServiceImpl
import cz.fit.metacentrum.service.api.ActionService
import javax.inject.Inject

/**
 * Action cron service is service that handles cron registration
 * @author Jakub Tucek
 */
class ActionCronService : ActionService<ActionCron> {

    @Inject
    private lateinit var shellServiceImpl: ShellServiceImpl
    @Inject
    private lateinit var cronService: CronService

    override fun processAction(argumentAction: ActionCron) {
        when (argumentAction.actionType) {
            ActionCron.Type.START -> handleStartCron()
            ActionCron.Type.STOP -> handleStopCron()
            ActionCron.Type.RESTART -> {
                handleStopCron()
                handleStartCron()
            }
        }
    }

    private fun handleStopCron() {
        cronService.unregister()
    }

    private fun handleStartCron() {
        cronService.register()
    }
}