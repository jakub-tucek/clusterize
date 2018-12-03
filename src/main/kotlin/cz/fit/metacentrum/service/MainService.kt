package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.ActionList
import cz.fit.metacentrum.domain.ActionSubmit
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.input.CommandLineParser
import javax.inject.Inject


class MainService() {
    @Inject
    private lateinit var commandLineParser: CommandLineParser
    @Inject
    private lateinit var actionSubmitService: ActionService<ActionSubmit>
    @Inject
    private lateinit var actionListService: ActionService<ActionList>


    fun execute(args: Array<String>) {
        // parseConfig arguments
        val argumentAction = commandLineParser.parseArguments(args)


        when (argumentAction) {
            is ActionSubmit -> actionSubmitService.processAction(argumentAction)
            is ActionList -> actionListService.processAction(argumentAction)
            else -> throw IllegalArgumentException("Unknown action")
        }
    }
}