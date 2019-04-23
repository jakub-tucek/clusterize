package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.config.PythonTaskType
import cz.fit.metacentrum.domain.config.TaskType
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.JobInfo
import cz.fit.metacentrum.domain.template.TemplateData
import cz.fit.metacentrum.service.TemplateService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import javax.inject.Inject


/**
 * Executes script.
 * @author Jakub Tucek
 */
class ScriptBuilderExecutor : TaskExecutor {

    @Inject
    private lateinit var templateDataBuilder: TemplateDataBuilder
    @Inject
    private lateinit var templateService: TemplateService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Generating bash scripts")
        val iterationCombinations = when {
            metadata.iterationCombinations == null -> throw IllegalStateException("Iteration combination is not initialized")
            metadata.iterationCombinations.isEmpty() -> listOf(emptyMap())
            else -> metadata.iterationCombinations
        }

        val variableData = HashMap<String, String>()
        variableData.putAll(metadata.configFile.general.variables!!)

        // helper variables for printing status
        val totalSize = iterationCombinations.size
        var lastMessage = ""
        val step = Math.ceil(totalSize / 10.0).toInt()

        val submittedJobs = iterationCombinations.mapIndexed { index, iterationCombination ->
            variableData.putAll(iterationCombination)
            val template = createTemplate(metadata, variableData, index)

            if (index % step == 0) {
                ConsoleWriter.deleteStatusDetail(lastMessage)
                lastMessage = "Generated $index/$totalSize"
                ConsoleWriter.writeStatusDetail(lastMessage, newline = false)
            }

            template
        }
        println()
        ConsoleWriter.writeStatusDetail("Generated $totalSize/$totalSize. All script generated")


        return metadata.copy(jobs = submittedJobs)
    }

    private fun createTemplate(metadata: ExecutionMetadata,
                               variableData: HashMap<String, String>,
                               runCounter: Int): ExecutionMetadataJob {
        @Suppress("REDUNDANT_ELSE_IN_WHEN") val templateData = when (metadata.configFile.taskType) {
            is MatlabTaskType -> {
                val templateData = templateDataBuilder
                        .prepare<MatlabTaskType>(metadata, variableData, runCounter)
                writeData("matlab.mustache", templateData)
                templateData

            }
            is PythonTaskType -> {
                val templateData = templateDataBuilder
                        .prepare<PythonTaskType>(metadata, variableData, runCounter)
                writeData("python.mustache", templateData)
                templateData
            }
            else -> throw IllegalStateException("Unknown state")
        }
        return ExecutionMetadataJob(
                jobPath = templateData.runPath,
                jobId = runCounter,
                jobInfo = JobInfo.empty()
        )
    }

    private fun <T : TaskType> writeData(templateName: String, data: TemplateData<T>) {
        val innerScriptPath = data.runPath.resolve(FileNames.innerScript)
        templateService.write("templates/$templateName", innerScriptPath, data)
    }

}