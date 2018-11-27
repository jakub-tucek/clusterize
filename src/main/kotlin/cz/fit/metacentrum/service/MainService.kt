package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.ActionList
import cz.fit.metacentrum.domain.ActionSubmit
import javax.inject.Inject


class MainService() {
    @Inject
    private lateinit var commandLineParser: CommandLineParser
    @Inject
    private lateinit var actionSubmitService: ActionSubmitService
    @Inject
    private lateinit var actionListService: ActionListService


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