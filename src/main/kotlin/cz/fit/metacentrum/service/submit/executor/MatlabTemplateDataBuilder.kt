package cz.fit.metacentrum.service.submit.executor

import cz.fit.metacentrum.domain.TemplateDataGeneral
import cz.fit.metacentrum.domain.TemplateDataMatlab
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import java.nio.file.Files

/**
 * Builder for template parameters.
 * @author Jakub Tucek
 */
class MatlabTemplateDataBuilder {


    fun prepare(metadata: ExecutionMetadata,
                variableData: HashMap<String, String>,
                runCounter: Int): TemplateDataMatlab {
        val taskType = metadata.configFile.taskType as MatlabTaskType
        val sourcesPath = metadata.paths.sourcesPath?.toAbsolutePath()
                ?: throw IllegalStateException("Sources path not set")

        // get run path, initialize folder
        val runPath = metadata.paths.storagePath
                ?.resolve(runCounter.toString())
                ?.toAbsolutePath()
                ?: throw IllegalStateException("Couldn't create run path")
        Files.createDirectories(runPath)

        return TemplateDataMatlab(
                taskType,
                variableData.toSortedMap().toList(),
                TemplateDataGeneral(
                        dependents = metadata.configFile.general.dependents,
                        taskName = metadata.configFile.general.taskName
                ),
                runPath,
                sourcesPath,
                metadata.configFile.resources.modules,
                metadata.configFile.resources.toolboxes
        )
    }
}