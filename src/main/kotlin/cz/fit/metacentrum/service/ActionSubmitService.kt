package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.ActionSubmit
import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.validator.ConfigValidationService
import cz.fit.metacentrum.util.ConsoleWriter
import javax.inject.Inject

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
        ConsoleWriter.writeExecutorsRunEnd()
        val finalMetadata = executorSet.asSequence()
                .fold(initMetadata) { metadata, executor ->
                    val res = executor.execute(metadata)
                    ConsoleWriter.writeStatusDone()
                    res
                }
        ConsoleWriter.writeExecutorsRunEnd()
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
}