package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.*
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.input.CommandLineParser
import javax.inject.Inject

/**
 * Main service that processes user arguments and executors correct action.
 */
class MainService() {
    @Inject
    private lateinit var commandLineParser: CommandLineParser
    @Inject
    private lateinit var actionSubmitService: ActionService<ActionSubmit>
    @Inject
    private lateinit var actionStatusService: ActionService<ActionStatus>
    @Inject
    private lateinit var actionDaemonService: ActionService<ActionDaemon>


    fun execute(args: Array<String>) {
        // parseConfig arguments
        val argumentAction = commandLineParser.parseArguments(args)


        when (argumentAction) {
            is ActionSubmit -> actionSubmitService.processAction(argumentAction)
            is ActionStatus -> actionStatusService.processAction(argumentAction)
            is ActionDaemon -> actionDaemonService.processAction(argumentAction)
            is ActionResubmitFailed -> TODO()
            is ActionHelp -> {
                // dont do anything
            }
        }
    }
}