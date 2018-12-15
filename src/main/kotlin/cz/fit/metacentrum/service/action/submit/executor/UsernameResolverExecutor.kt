package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import mu.KotlinLogging
import javax.inject.Inject


private val logger = KotlinLogging.logger { }

/**
 * Executor that retrieves current username and sets it to metadata
 * @author Jakub Tucek
 */
class UsernameResolverExecutor : TaskExecutor {

    @Inject
    private lateinit var shellService: ShellService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        // use whoami instead of using system property in case execution is done remotely (like in docker container)
        val (output, status, errOutput) = shellService.runCommand("whoami")
        if (status != 0) {
            logger.info("Retrieving username failed with status ${status}. ${errOutput}")
        }
        return metadata.copy(
                submittingUsername = output
        )
    }

}