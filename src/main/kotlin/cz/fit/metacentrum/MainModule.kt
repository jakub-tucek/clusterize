package cz.fit.metacentrum

import com.google.inject.AbstractModule
import cz.fit.metacentrum.service.ActionSubmitService
import cz.fit.metacentrum.service.CommandLineParser
import cz.fit.metacentrum.service.ConfigFileParser
import cz.fit.metacentrum.service.MainService
import cz.fit.metacentrum.service.validator.ConfigValidationService
import cz.fit.metacentrum.service.validator.IterationConfigValidator


class MainModule : AbstractModule() {

    override fun configure() {
        bind(MainService::class.java)

        bind(ConfigFileParser::class.java)
        bind(CommandLineParser::class.java)

        // validator
        bind(ConfigValidationService::class.java)
        bind(IterationConfigValidator::class.java)

        bind(ActionSubmitService::class.java)
    }
}