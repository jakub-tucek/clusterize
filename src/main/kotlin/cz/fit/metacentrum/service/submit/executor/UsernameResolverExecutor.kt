package cz.fit.metacentrum.service.submit.executor

import com.google.inject.Inject
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.api.ShellService
import cz.fit.metacentrum.service.api.TaskExecutor
import mu.KotlinLogging


private val logger = KotlinLogging.logger { }

/**
 *
 * @author Jakub Tucek
 */
class UsernameResolverExecutor : TaskExecutor {

    @Inject
    private lateinit var shellService: ShellService

    override fun execute(metadata: ExecutionMetadata): ExecutionMetadata {
        val (output, status, errOutput) = shellService.runCommand("whoami")
        if (status != 0) {
            logger.info("Retrieving username failed with status ${status}. ${errOutput}")
        }
        return metadata.copy(
                submittingUsername = output
        )
    }

}