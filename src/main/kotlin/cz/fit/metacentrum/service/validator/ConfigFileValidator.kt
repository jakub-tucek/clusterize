package cz.fit.metacentrum.service.validator

import cz.fit.metacentrum.domain.ConfigFile
import cz.fit.metacentrum.domain.ValidationResult


class ConfigFileValidator {

    fun validate(configFile: ConfigFile): ValidationResult {
        val iterationsRes = ConfigIterationValidator().validate(configFile.iterations)

        return iterationsRes
    }
}