package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.ArgumentInput
import cz.fit.metacentrum.domain.ValidationResult
import cz.fit.metacentrum.service.validator.ConfigFileValidator
import javax.inject.Inject


class MetacentrumCliService() {
    @Inject
    private lateinit var commandLineParser: CommandLineParser
    @Inject
    private lateinit var configFileParser: ConfigFileParser
    @Inject
    private lateinit var configFileValidator: ConfigFileValidator

    fun execute(args: Array<String>) {
        // parse arguments
        val parsedArgs = commandLineParser.parseArguments(args)
        println(parsedArgs)
        val config = getConfig(parsedArgs)
    }

    private fun getConfig(parsedArgs: ArgumentInput): ValidationResult {
        // parse configuration file
        val parsedConfig = configFileParser.parse(parsedArgs.configFile)
        println(parsedConfig)
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