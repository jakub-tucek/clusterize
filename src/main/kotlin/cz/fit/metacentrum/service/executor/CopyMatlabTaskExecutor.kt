package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ConfigFile
import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.MatlabTaskType
import cz.fit.metacentrum.service.api.TaskExecutor

/**
 *
 * @author Jakub Tucek
 */
class CopyMatlabTaskExecutor : TaskExecutor {
    override fun execute(configFile: ConfigFile, metadata: ExecutionMetadata): ExecutionMetadata {
        val matlabAction = configFile.taskType as MatlabTaskType



        return metadata
    }

}