package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.ActionSubmit
import cz.fit.metacentrum.domain.ValidationResult
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.validator.ConfigFileValidator
import javax.inject.Inject

/**
 *
 * @author Jakub Tucek
 */
class ActionSubmitService() : ActionService<ActionSubmit> {
    @Inject
    private lateinit var configFileParser: ConfigFileParser
    @Inject
    private lateinit var configFileValidator: ConfigFileValidator

    override fun processAction(argumentAction: ActionSubmit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun getConfig(parsedArgs: ActionSubmit): ValidationResult {
        // parse configuration file
        val parsedConfig = configFileParser.parse(parsedArgs.configFile)
        // validate configuration file values
        val validationResult = configFileValidator.validate(parsedConfig)
        // failed if not set
        if (!validationResult.success) {
            System.err.println(validationResult.messages.joinToString("\n"))
            System.exit(1)
        }
        return validationResult
    }
}