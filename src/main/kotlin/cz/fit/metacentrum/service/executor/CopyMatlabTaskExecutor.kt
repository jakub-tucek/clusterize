package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ConfigFile
import cz.fit.metacentrum.domain.ExecutionResult
import cz.fit.metacentrum.domain.MatlabTaskType
import cz.fit.metacentrum.service.api.TaskExecutor

/**
 *
 * @author Jakub Tucek
 */
class CopyMatlabTaskExecutor : TaskExecutor {
    override fun execute(configFile: ConfigFile): ExecutionResult {
        val matlabAction = configFile.taskType as MatlabTaskType

        // TOOD implement copy matlab files

        return ExecutionResult()
    }

}