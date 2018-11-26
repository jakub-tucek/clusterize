package cz.fit.metacentrum.domain

import cz.fit.metacentrum.domain.config.ConfigEnvironmentDependent
import cz.fit.metacentrum.domain.config.MatlabTaskType

/**
 *
 * @author Jakub Tucek
 */
data class MatlabTemplateData(
        val taskType: MatlabTaskType,
        val variables: List<Pair<String, String>>,
        val functionParams: String,
        val dependentVariables: List<ConfigEnvironmentDependent>,
        val storagePath: String
)