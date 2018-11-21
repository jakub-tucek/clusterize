package cz.fit.metacentrum.service.validator

import cz.fit.metacentrum.domain.ConfigFile
import cz.fit.metacentrum.domain.ValidationResult
import javax.inject.Inject


class ConfigFileValidator {

    @Inject
    private lateinit var configIterationValidator: ConfigIterationValidator

    fun validate(configFile: ConfigFile): ValidationResult {
        val iterationsRes = configIterationValidator.validate(configFile.iterations)

        return iterationsRes
    }
}