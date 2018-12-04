package cz.fit.metacentrum.service.api

import cz.fit.metacentrum.domain.meta.ExecutionMetadata

/**
 * TaskExecutor is interface that defines and performs one execution step while running some action.
 * @author Jakub Tucek
 */
interface TaskExecutor {

    /**
     * Performs execution and saves result to metadata.
     */
    fun execute(metadata: ExecutionMetadata): ExecutionMetadata

}