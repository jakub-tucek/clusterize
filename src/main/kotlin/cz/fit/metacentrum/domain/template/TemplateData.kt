package cz.fit.metacentrum.domain.template

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.config.TaskType
import cz.fit.metacentrum.domain.meta.JobInfoFilePropNames
import java.nio.file.Path

/**
 * Matlab specific template data
 * @author Jakub Tucek
 */
data class TemplateData<T : TaskType>(
        val taskType: T,
        val variables: List<Pair<String, String>>,
        val general: GeneralTemplateData,
        val runPath: Path,
        val sourcesPath: Path,
        val resources: ResourcesTemplateData,
        val fileNames: FileNames = FileNames,
        val jobInfoProps: JobInfoFilePropNames = JobInfoFilePropNames
)

