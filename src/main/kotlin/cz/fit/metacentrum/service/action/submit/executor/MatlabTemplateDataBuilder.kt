package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.domain.config.ConfigResources
import cz.fit.metacentrum.domain.config.ConfigResourcesDetails
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.template.TemplateDataGeneral
import cz.fit.metacentrum.domain.template.TemplateDataMatlab
import cz.fit.metacentrum.domain.template.TemplateResources
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
                buildTemplateResources(metadata.configFile.resources)

        )
    }

    private fun buildTemplateResources(resources: ConfigResources): TemplateResources {
        val details = resources.details ?: throw IllegalStateException("Resource details configuration is missing")
        return TemplateResources(
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