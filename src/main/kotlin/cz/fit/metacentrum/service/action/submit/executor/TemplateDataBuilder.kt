package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.domain.config.ConfigResources
import cz.fit.metacentrum.domain.config.ConfigResourcesDetails
import cz.fit.metacentrum.domain.config.TaskType
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.template.GeneralTemplateData
import cz.fit.metacentrum.domain.template.ResourcesTemplateData
import cz.fit.metacentrum.domain.template.TemplateData
import java.nio.file.Files

/**
 * Builder for template parameters.
 * @author Jakub Tucek
 */
class TemplateDataBuilder {


    fun <T : TaskType> prepare(metadata: ExecutionMetadata,
                               variableData: HashMap<String, String>,
                               runCounter: Int): TemplateData<T> {
        @Suppress("UNCHECKED_CAST")
        val taskType = metadata.configFile.taskType as? T
                ?: throw IllegalStateException("Task type cannot be cased to given type")
        val sourcesPath = metadata.paths.sourcesPath?.toAbsolutePath()
                ?: throw IllegalStateException("Sources path not set")

        // get run path, initialize folder
        val runPath = metadata.paths.storagePath
                ?.resolve(runCounter.toString())
                ?.toAbsolutePath()
                ?: throw IllegalStateException("Couldn't create run path")
        Files.createDirectories(runPath)

        return TemplateData<T>(
                taskType,
                variableData.toSortedMap().toList(),
                GeneralTemplateData(
                        dependents = metadata.configFile.general.dependents,
                        taskName = metadata.configFile.general.taskName
                ),
                runPath,
                sourcesPath,
                buildTemplateResources(metadata.configFile.resources)

        )
    }

    private fun buildTemplateResources(resources: ConfigResources): ResourcesTemplateData {
        val details = resources.details ?: throw IllegalStateException("Resource details configuration is missing")
        return ResourcesTemplateData(
                walltime = details.walltime,
                formattedResources = formatResources(details),
                ncpus = details.ncpus,
                modules = resources.modules,
                toolboxes = resources.toolboxes
        )
    }

    private fun formatResources(details: ConfigResourcesDetails): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("select=${details.chunks}")
        stringBuilder.append(":ncpus=${details.ncpus}")
        stringBuilder.append(":mem=${details.mem}")
        stringBuilder.append(":scratch_local=${details.scratchLocal}")
        return stringBuilder.toString()
    }
}