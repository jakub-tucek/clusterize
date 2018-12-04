package cz.fit.metacentrum.service.submit.executor

import com.github.mustachejava.DefaultMustacheFactory
import com.google.inject.Inject
import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.StandardOpenOption


/**
 * Executes matlab script.
 * @author Jakub Tucek
 */
class MatlabScriptsExecutor : TaskExecutor {

    @Inject
    private lateinit var matlabTemplateDataBuilder: MatlabTemplateDataBuilder

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
        val mf = DefaultMustacheFactory()
        val mustache = mf.compile("templates/matlab.mustache")
        val templateStr = StringWriter()

        // prepare template data and use them
        val templateData = matlabTemplateDataBuilder.prepare(metadata, variableData, runCounter)

        mustache.execute(templateStr, templateData).flush()

        val innerScriptPath = templateData.runPath.resolve(FileNames.innerScript)
        Files.write(innerScriptPath, templateStr.buffer.lines(), StandardOpenOption.CREATE_NEW)

        return ExecutionMetadataJob(
                jobPath = templateData.runPath,
                jobId = runCounter
        )
    }

}