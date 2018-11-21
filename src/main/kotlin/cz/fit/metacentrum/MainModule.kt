package cz.fit.metacentrum

import com.google.inject.AbstractModule
import cz.fit.metacentrum.service.ActionSubmitService
import cz.fit.metacentrum.service.CommandLineParser
import cz.fit.metacentrum.service.ConfigFileParser
import cz.fit.metacentrum.service.MainService
import cz.fit.metacentrum.service.validator.ConfigFileValidator
import cz.fit.metacentrum.service.validator.ConfigIterationValidator


class MainModule : AbstractModule() {

    override fun configure() {
        bind(MainService::class.java)

        bind(ConfigFileParser::class.java)
        bind(CommandLineParser::class.java)

        // validator
        bind(ConfigFileValidator::class.java)
        bind(ConfigIterationValidator::class.java)

        bind(ActionSubmitService::class.java)
    }
}