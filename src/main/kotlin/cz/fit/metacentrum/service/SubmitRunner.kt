package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.SerializationService
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.FileUtils
import mu.KotlinLogging
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

/**
 * Provides option to safely run executors for task that is supposed to submitted. Deletes files if some executors
 * failed.
 * @author Jakub Tucek
 */
class SubmitRunner {
    @Inject
    private lateinit var serializationService: SerializationService

    fun run(initialMetadata: ExecutionMetadata, executors: Set<TaskExecutor>) {
        ConsoleWriter.writeDelimiter()
        val finalMetadata = executors.asSequence()
                .fold(initialMetadata) { metadata, executor -> safelyExecute(metadata, executor) }
        ConsoleWriter.writeDelimiter()
        serializationService.persistMetadata(finalMetadata)
    }

    private fun safelyExecute(metadata: ExecutionMetadata, executor: TaskExecutor): ExecutionMetadata {
        try {
            val res = executor.execute(metadata)
            ConsoleWriter.writeStatusDone()
            return res
        } catch (e: Exception) {
            logger.error("Executor in submit service failed. Cleaning up")
            cleanup(metadata)
            throw e
        }
    }

    private fun cleanup(metadata: ExecutionMetadata) {
        if (metadata.jobsHistory.isNotEmpty()) {
            return
        }
        if (metadata.paths.storagePath != null) {
            FileUtils.deleteFolder(metadata.paths.storagePath)
        }
        if (metadata.paths.metadataStoragePath != null) {
            FileUtils.deleteFolder(metadata.paths.metadataStoragePath)
        }
    }
}