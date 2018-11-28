package cz.fit.metacentrum.service.api

import cz.fit.metacentrum.domain.meta.ExecutionMetadata

/**
 * TaskExecutor is interface that defines and performs one execution step while running some action.
 * @author Jakub Tucek
 */
interface TaskExecutor {

    /**
     * Performs execution based on given valid configuration file.
     */
    fun execute(metadata: ExecutionMetadata): ExecutionMetadata

}