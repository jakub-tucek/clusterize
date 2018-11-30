package cz.fit.metacentrum.service.submit.executor

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.TaskExecutor

/**
 *
 * @author Jakub Tucek
 */
class UsernameResolverExecutor : TaskExecutor {
    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        return metadata.copy(
                submittingUsername = System.getProperty("user.name")
        )
    }

}