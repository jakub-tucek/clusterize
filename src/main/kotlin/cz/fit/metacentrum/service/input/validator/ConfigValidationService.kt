package cz.fit.metacentrum.service.input.validator

import cz.fit.metacentrum.domain.ValidationResult
import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.service.api.ConfigValidator
import javax.inject.Inject


class ConfigValidationService {

    @Inject
    private lateinit var configValidators: Set<@JvmSuppressWildcards ConfigValidator>


    fun validate(configFile: ConfigFile): ValidationResult {
        return configValidators
                .map { it.validate(configFile) }
                .reduce { a1, a2 -> a1 + a2 }
    }
}