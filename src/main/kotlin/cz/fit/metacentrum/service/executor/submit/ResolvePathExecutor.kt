package cz.fit.metacentrum.service.executor.submit

import cz.fit.metacentrum.domain.ExecutionMetadata
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
        val newBasePath = FileUtils.relativizePath(config.environment.metadataStoragePath)

        val newConfig = config.copy(
                environment = config.environment.copy(metadataStoragePath = newBasePath)
        )
        return ExecutionMetadata(configFile = newConfig)
    }

}