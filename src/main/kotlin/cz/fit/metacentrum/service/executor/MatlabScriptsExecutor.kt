package cz.fit.metacentrum.service.executor

import com.github.mustachejava.DefaultMustacheFactory
import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.MatlabTaskType
import cz.fit.metacentrum.domain.TemplateData
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.TemplateUtils
import java.io.PrintWriter


/**
 *
 * @author Jakub Tucek
 */
class MatlabScriptsExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val taskType = metadata.configFile.taskType as MatlabTaskType
        val mf = DefaultMustacheFactory()
        val mustache = mf.compile("templates/matlab.mustache")
        mustache.execute(PrintWriter(System.out),
                TemplateData(
                        metadata.configFile.taskType,
                        mapOf("ONE" to "value").toSortedMap().toList(),
                        TemplateUtils.formatAsFunctionParams(taskType.parameters)
                )
        ).flush()

        return metadata
    }
}