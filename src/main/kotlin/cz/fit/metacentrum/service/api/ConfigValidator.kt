package cz.fit.metacentrum.service.api

import cz.fit.metacentrum.domain.ConfigFile
import cz.fit.metacentrum.domain.ValidationResult

/**
 *
 * @author Jakub Tucek
 */
interface ConfigValidator {

    fun validate(configFile: ConfigFile): ValidationResult

}