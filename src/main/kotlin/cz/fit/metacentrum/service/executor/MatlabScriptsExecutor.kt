package cz.fit.metacentrum.service.executor

import com.github.mustachejava.DefaultMustacheFactory
import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.MatlabTemplateData
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.TemplateUtils
import java.io.PrintWriter
import java.nio.file.Files


/**
 *
 * @author Jakub Tucek
 */
class MatlabScriptsExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val iterationCombinations = when {
            metadata.iterationCombinations == null -> throw IllegalStateException("Iteration combination is not initialized")
            metadata.iterationCombinations.isEmpty() -> listOf(emptyMap())
            else -> metadata.iterationCombinations
        }

        val variableData = HashMap<String, String>()
        variableData.putAll(metadata.configFile.environment.variables ?: emptyMap())

        for ((index, iteration) in iterationCombinations.withIndex()) {
            variableData.putAll(iteration)
            createTemplate(metadata, variableData, index)
        }



        return metadata
    }

    private fun createTemplate(metadata: ExecutionMetadata, variableData: HashMap<String, String>, runCounter: Int) {
        val taskType = metadata.configFile.taskType as MatlabTaskType

        val runPath = metadata.metadataStoragePath?.resolve(runCounter.toString())
                ?: throw IllegalStateException("Couldn't create run path")
        if (!Files.exists(runPath)) Files.createDirectories(runPath)

        val mf = DefaultMustacheFactory()
        val mustache = mf.compile("templates/matlab.mustache")
        mustache.execute(PrintWriter(System.out),
                MatlabTemplateData(
                        taskType,
                        variableData.toSortedMap().toList(),
                        TemplateUtils.formatFunctionParameters(taskType.parameters),
                        metadata.configFile.environment.dependents,
                        runPath.toAbsolutePath().toString()
                )
        ).flush()

    }
}