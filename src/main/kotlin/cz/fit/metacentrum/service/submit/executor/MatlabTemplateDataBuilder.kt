package cz.fit.metacentrum.service.submit.executor

import cz.fit.metacentrum.config.ModuleConfiguration
import cz.fit.metacentrum.config.ProfileConfiguration
import cz.fit.metacentrum.domain.MatlabTemplateData
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.util.TemplateUtils
import java.nio.file.Files

/**
 * Builder for template parameters.
 * @author Jakub Tucek
 */
class MatlabTemplateDataBuilder {


    fun prepare(metadata: ExecutionMetadata,
                variableData: HashMap<String, String>,
                runCounter: Int): MatlabTemplateData {
        val taskType = metadata.configFile.taskType as MatlabTaskType
        val sourcesPath = metadata.paths.sourcesPath?.toAbsolutePath()
                ?: throw IllegalStateException("Sources path not set")

        // get run path, initialize folder
        val runPath = metadata.paths.storagePath
                ?.resolve(runCounter.toString())
                ?.toAbsolutePath()
                ?: throw IllegalStateException("Couldn't create run path")
        Files.createDirectories(runPath)

        return MatlabTemplateData(
                taskType,
                variableData.toSortedMap().toList(),
                TemplateUtils.formatFunctionParameters(taskType.parameters),
                metadata.configFile.general.dependents,
                runPath,
                sourcesPath,
                ModuleConfiguration.matlabModule,
                retrieveToolBoxes(taskType)
        )
    }

    fun retrieveToolBoxes(taskType: MatlabTaskType): List<String> {
        if (ProfileConfiguration.isDev()) {
            return emptyList()
        } else {
            return listOf(ModuleConfiguration.defaultToolbox, *taskType.modules.toTypedArray())
        }
    }
}