package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import java.nio.file.Paths

/**
 * Copy sources to metadata directory
 * @author Jakub Tucek
 */
class CopySourcesFilesExecutor : TaskExecutor {

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Preparing sources files from ${metadata.configFile.general.sourcesPath}")


        return metadata.copy(paths = metadata.paths.copy(sourcesPath = Paths.get(metadata.configFile.general.sourcesPath)))
    }

}