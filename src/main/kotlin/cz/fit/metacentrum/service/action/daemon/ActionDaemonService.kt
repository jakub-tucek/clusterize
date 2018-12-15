package cz.fit.metacentrum.service.action.daemon

import cz.fit.metacentrum.domain.ActionDaemon
import cz.fit.metacentrum.service.ShellServiceImpl
import cz.fit.metacentrum.service.api.ActionService
import javax.inject.Inject

/**
 * Action daemon service that works with background daemon
 * @author Jakub Tucek
 */
class ActionDaemonService : ActionService<ActionDaemon> {

    @Inject
    private lateinit var shellServiceImpl: ShellServiceImpl
    @Inject
    private lateinit var daemonService: DaemonService

    override fun processAction(argumentAction: ActionDaemon) {
        when (argumentAction.actionType) {
            ActionDaemon.Type.START -> handleStartDaemon()
            ActionDaemon.Type.STOP -> handleStopDaemon()
        }
    }

    private fun handleStopDaemon() {
        daemonService.killDaemon()
    }

    private fun handleStartDaemon() {
        daemonService.execDaemon()
    }
}