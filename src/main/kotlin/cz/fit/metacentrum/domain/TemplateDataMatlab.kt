package cz.fit.metacentrum.domain

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.config.MatlabTaskType
import java.nio.file.Path

/**
 *
 * @author Jakub Tucek
 */
data class TemplateDataMatlab(
        val taskType: MatlabTaskType,
        val variables: List<Pair<String, String>>,
        val general: TemplateDataGeneral,
        val runPath: Path,
        val sourcesPath: Path,
        val modules: Set<String>, // name of loaded module
        val toolboxes: Set<String>, // name of used toolboxes
        val fileNames: FileNames = FileNames
)

