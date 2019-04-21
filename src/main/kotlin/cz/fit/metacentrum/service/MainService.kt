package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.*
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.input.CommandLineParser
import javax.inject.Inject

/**
 * Main service that processes user arguments and executors correct action.
 */
class MainService {
    @Inject
    private lateinit var commandLineParser: CommandLineParser
    @Inject
    private lateinit var actionSubmitService: ActionService<ActionSubmit>
    @Inject
    private lateinit var actionStatusService: ActionService<ActionStatus>
    @Inject
    private lateinit var actionCronService: ActionService<ActionCron>
    @Inject
    private lateinit var actionCronInternalService: ActionService<ActionCronStartInternal>
    @Inject
    private lateinit var actionResubmitService: ActionService<ActionResubmit>
    @Inject
    private lateinit var actionAnalyzeService: ActionService<ActionAnalyze>
    @Inject
    private lateinit var actionVersionService: ActionService<ActionVersion>

    fun execute(args: Array<String>) {
        // parseConfig arguments
        val argumentAction = commandLineParser.parseArguments(args)


        when (argumentAction) {
            is ActionSubmit -> actionSubmitService.processAction(argumentAction)
            is ActionStatus -> actionStatusService.processAction(argumentAction)
            is ActionCron -> actionCronService.processAction(argumentAction)
            is ActionCronStartInternal -> actionCronInternalService.processAction(argumentAction)
            is ActionResubmit -> actionResubmitService.processAction(argumentAction)
            is ActionAnalyze -> actionAnalyzeService.processAction(argumentAction)
            is ActionVersion -> actionVersionService.processAction(argumentAction)
            is ActionHelp -> {
                // dont do anything
            }
        }
    }
}