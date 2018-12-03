package cz.fit.metacentrum.service.submit

import cz.fit.metacentrum.domain.ActionSubmit
import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.SerializationService
import cz.fit.metacentrum.service.input.validator.ConfigValidationService
import cz.fit.metacentrum.util.ConsoleWriter
import cz.fit.metacentrum.util.FileUtils
import mu.KotlinLogging
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

/**
 *
 * @author Jakub Tucek
 */
class ActionSubmitService() : ActionService<ActionSubmit> {
    @Inject
    private lateinit var serializationService: SerializationService
    @Inject
    private lateinit var configValidationService: ConfigValidationService
    @Inject
    private lateinit var matlabExecutors: Set<@JvmSuppressWildcards TaskExecutor>

    override fun processAction(argumentAction: ActionSubmit) {
        val config = getConfig(argumentAction)

        when (config.taskType) {
            is MatlabTaskType -> runExecutors(config, matlabExecutors)
        }

    }

    private fun runExecutors(config: ConfigFile, executorSet: Set<TaskExecutor>) {
        val initMetadata = ExecutionMetadata(configFile = config)
        ConsoleWriter.writeDelimiter()
        val finalMetadata = executorSet.asSequence()
                .fold(initMetadata) { metadata, executor -> safelyExecute(metadata, executor) }
        ConsoleWriter.writeDelimiter()
        serializationService.persistMetadata(finalMetadata)
    }


    private fun getConfig(parsedArgs: ActionSubmit): ConfigFile {
        // parseConfig configuration file
        val parsedConfig = serializationService.parseConfig(parsedArgs.configFile)
        // validate configuration file values
        val validationResult = configValidationService.validate(parsedConfig)
        // failed if not set
        if (!validationResult.success) {
            System.err.println(validationResult.messages.joinToString("\n"))
            System.exit(1)
        }
        return parsedConfig
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
        if (metadata.storagePath != null) {
            FileUtils.deleteFolder(metadata.storagePath)
        }
        if (metadata.metadataStoragePath != null) {
            FileUtils.deleteFolder(metadata.metadataStoragePath)
        }
    }
}