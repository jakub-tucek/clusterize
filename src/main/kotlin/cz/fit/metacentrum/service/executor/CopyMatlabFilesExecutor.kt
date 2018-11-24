package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.MatlabTaskType
import cz.fit.metacentrum.service.api.TaskExecutor

/**
 *
 * @author Jakub Tucek
 */
class CopyMatlabFilesExecutor : TaskExecutor {
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val matlabAction = metadata.configFile.taskType as MatlabTaskType


        return metadata
    }

}