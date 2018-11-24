package cz.fit.metacentrum.service.api

import cz.fit.metacentrum.domain.ConfigFile
import cz.fit.metacentrum.domain.ValidationResult

/**
 * ConfigValidator is interface that specifies type of validation that is done upon given configuration file.
 * @author Jakub Tucek
 */
interface ConfigValidator {

    fun validate(configFile: ConfigFile): ValidationResult

}