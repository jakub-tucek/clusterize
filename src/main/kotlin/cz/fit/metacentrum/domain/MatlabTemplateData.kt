package cz.fit.metacentrum.domain

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.config.ConfigEnvironmentDependent
import cz.fit.metacentrum.domain.config.MatlabTaskType
import java.nio.file.Path

/**
 *
 * @author Jakub Tucek
 */
data class MatlabTemplateData(
        val taskType: MatlabTaskType,
        val variables: List<Pair<String, String>>,
        val functionParams: String,
        val dependentVariables: List<ConfigEnvironmentDependent>,
        val runPath: Path,
        val sourcesPath: Path,
        val moduleName: String, // name of loaded module
        val toolboxes: List<String>, // name of used toolboxes
        val fileNames: FileNames = FileNames
)