package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ConfigFile
import cz.fit.metacentrum.domain.ExecutionResult
import cz.fit.metacentrum.domain.TaskTypeMatlab
import cz.fit.metacentrum.service.api.ActionExecutor

/**
 *
 * @author Jakub Tucek
 */
class CopyMatlabActionExecutor : ActionExecutor {
    override fun execute(configFile: ConfigFile): ExecutionResult {
        val matlabAction = configFile.taskType as TaskTypeMatlab

        // TOOD implement copy matlab files

        return ExecutionResult()
    }

}