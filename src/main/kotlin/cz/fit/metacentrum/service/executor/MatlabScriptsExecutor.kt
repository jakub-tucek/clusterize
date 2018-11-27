package cz.fit.metacentrum.service.executor

import com.github.mustachejava.DefaultMustacheFactory
import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.MatlabTemplateData
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.TemplateUtils
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path


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

        val runPath = initializePath(metadata.storagePath).resolve(runCounter.toString())
        val metadataScriptRunPath = initializePath(metadata.metadataStoragePath).resolve("generated_scripts")
        Files.createDirectories(metadataScriptRunPath)

        val mf = DefaultMustacheFactory()
        val mustache = mf.compile("templates/matlab.mustache")
        val templateStr = StringWriter()

        mustache.execute(templateStr,
                MatlabTemplateData(
                        taskType,
                        variableData.toSortedMap().toList(),
                        TemplateUtils.formatFunctionParameters(taskType.parameters),
                        metadata.configFile.environment.dependents,
                        runPath.toAbsolutePath().toString(),
                        metadata.sourcesPath?.toAbsolutePath() ?: throw IllegalStateException("Sources path not set")
                )
        ).flush()

        val innerScriptPath = metadataScriptRunPath.resolve("${runCounter}_inner_script.sh")
        Files.createFile(innerScriptPath)
        Files.write(innerScriptPath, templateStr.buffer.lines())

        println(templateStr)
    }

    private fun initializePath(path: Path?): Path {
        val initializedPath = path
                ?: throw IllegalStateException("Couldn't create run path")
        return initializedPath
    }
}