package cz.fit.metacentrum.service.action.submit

import com.google.inject.name.Named
import cz.fit.metacentrum.config.actionSubmitExecutors
import cz.fit.metacentrum.domain.ActionSubmit
import cz.fit.metacentrum.domain.ActionSubmitConfig
import cz.fit.metacentrum.domain.ActionSubmitPath
import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.config.PythonTaskType
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.SubmitRunner
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.config.ConfiguratorRunner
import cz.fit.metacentrum.service.input.SerializationService
import cz.fit.metacentrum.service.input.validator.ConfigValidationService
import javax.inject.Inject

/**
 * Action Submit service implementation.
 * @author Jakub Tucek
 */
class ActionSubmitService : ActionService<ActionSubmit> {
    @Inject
    private lateinit var serializationService: SerializationService
    @Inject
    private lateinit var configValidationService: ConfigValidationService
    @Inject
    @Named(actionSubmitExecutors)
    private lateinit var submitExecutors: Set<@JvmSuppressWildcards TaskExecutor>
    @Inject
    private lateinit var configuratorRunner: ConfiguratorRunner
    @Inject
    private lateinit var submitRunner: SubmitRunner

    override fun processAction(argumentAction: ActionSubmit) {
        val config = when (argumentAction) {
            is ActionSubmitPath -> getConfig(argumentAction.configFilePath)
            is ActionSubmitConfig -> argumentAction.configFile
        }
        val interactiveConfig = configuratorRunner.runConfiguration(config)
        val initMetadata = ExecutionMetadata(configFile = interactiveConfig)

        @Suppress("REDUNDANT_ELSE_IN_WHEN")
        when (interactiveConfig.taskType) {
            is MatlabTaskType -> submitRunner.run(initMetadata, submitExecutors)
            is PythonTaskType -> submitRunner.run(initMetadata, submitExecutors)
            else -> throw IllegalStateException("Unknown task type")
        }

    }

    private fun getConfig(configFilePath: String): ConfigFile {
        // parseConfig configuration file
        val parsedConfig = serializationService.parseConfig(configFilePath)
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