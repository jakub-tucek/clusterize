package cz.fit.metacentrum.service.submit.executor

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.FileUtils

/**
 * Resolves paths so they can be used in Java/Kotlin API. Such example can be string path starting
 * with ~. Alters configuration file.
 * @author Jakub Tucek
 */
class ResolvePathExecutor : TaskExecutor {

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        ConsoleWriter.writeStatus("Processing and resolving given paths")
        val config = metadata.configFile

        // fix base path in env
        val newBasePath = FileUtils.relativizePath(config.general.metadataStoragePath)

        val newConfig = config.copy(
                general = config.general.copy(metadataStoragePath = newBasePath)
        )
        return ExecutionMetadata(configFile = newConfig)
    }

}