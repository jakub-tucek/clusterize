package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.util.FileUtil

/**
 * Resolves paths so they can be used in Java/Kotlin API. Such example can be string path starting
 * with ~. Alters configuration file.
 * @author Jakub Tucek
 */
class ResolvePathExecutor : TaskExecutor {

    @Suppress("REDUNDANT_ELSE_IN_WHEN")
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val config = metadata.configFile

        // fix base path in env
        val newBasePath = FileUtil.relativizePath(config.environment.basePath)

        // fix specific stuff
        val newTaskType = when (config.taskType) {
            is MatlabTaskType -> {
                val matlab = config.taskType
                val newMatlabFolder = FileUtil.relativizePath(matlab.matlabDir)
                matlab.copy(matlabDir = newMatlabFolder)
            }
            else -> throw IllegalStateException("Unexpected MatlabTaskType type")
        }

        val newConfig = config.copy(
                taskType = newTaskType,
                environment = config.environment.copy(basePath = newBasePath)
        )
        return ExecutionMetadata(configFile = newConfig)
    }

}