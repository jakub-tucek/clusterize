package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.JobInfo
import cz.fit.metacentrum.service.TemplateService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import javax.inject.Inject


/**
 * Executes matlab script.
 * @author Jakub Tucek
 */
class MatlabScriptsExecutor : TaskExecutor {

    @Inject
    private lateinit var matlabTemplateDataBuilder: MatlabTemplateDataBuilder
    @Inject
    private lateinit var templateService: TemplateService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Generating bash scripts wrapping matlab")
        val iterationCombinations = when {
            metadata.iterationCombinations == null -> throw IllegalStateException("Iteration combination is not initialized")
            metadata.iterationCombinations.isEmpty() -> listOf(emptyMap())
            else -> metadata.iterationCombinations
        }

        val variableData = HashMap<String, String>()
        variableData.putAll(metadata.configFile.general.variables ?: emptyMap())

        val submittedJobs = iterationCombinations.mapIndexed { index, iterationCombination ->
            variableData.putAll(iterationCombination)
            createTemplate(metadata, variableData, index)
        }


        return metadata.copy(jobs = submittedJobs)
    }

    private fun createTemplate(metadata: ExecutionMetadata,
                               variableData: HashMap<String, String>,
                               runCounter: Int): ExecutionMetadataJob {
        // prepare template data and use them
        val templateData = matlabTemplateDataBuilder.prepare(metadata, variableData, runCounter)

        val innerScriptPath = templateData.runPath.resolve(FileNames.innerScript)
        templateService.write("templates/matlab.mustache", innerScriptPath, templateData)

        return ExecutionMetadataJob(
                jobPath = templateData.runPath,
                jobId = runCounter,
                jobInfo = JobInfo.empty()
        )
    }

}