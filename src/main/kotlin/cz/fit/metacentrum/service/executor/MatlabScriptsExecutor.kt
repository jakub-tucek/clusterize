package cz.fit.metacentrum.service.executor

import com.github.mustachejava.DefaultMustacheFactory
import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import java.io.PrintWriter


/**
 *
 * @author Jakub Tucek
 */
class MatlabScriptsExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val mf = DefaultMustacheFactory()
        val mustache = mf.compile("templates/matlab.mustache")
        mustache.execute(PrintWriter(System.out), metadata.configFile).flush()

        return metadata
    }
}