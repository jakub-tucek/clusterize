package cz.fit.metacentrum.service.action.resubmit

import cz.fit.metacentrum.domain.ActionResubmit
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.input.SerializationService
import java.nio.file.Paths
import javax.inject.Inject

/**
 * Service for resubmit failed jobs
 * @author Jakub Tucek
 */
class ActionResubmitService : ActionService<ActionResubmit> {


    @Inject
    private lateinit var serializationService: SerializationService
    @Inject
    private lateinit var resubmitService: ResubmitService

    override fun processAction(argumentAction: ActionResubmit) {
        val metadata = readTaskMetadata(argumentAction)

        val preparedMetadata = when {
            argumentAction.onlyFailed -> resubmitService.prepareForResubmit(metadata, this::resubmitFailed)
            else -> TODO("Resubmitting not failed tasks is not supported")
        }

        resubmitService.executeResubmit(preparedMetadata)
    }


    private fun resubmitFailed(it: ExecutionMetadataJob) = it.jobInfo.state == ExecutionMetadataState.FAILED


    private fun readTaskMetadata(argumentAction: ActionResubmit): ExecutionMetadata {
        val (taskId, metadataStoragePath) = argumentAction
        val appConfiguration = serializationService.parseMetadataIdPathMapping(
                Paths.get(metadataStoragePath)
        )
        val taskIdInt = taskId.toIntOrNull()
        if (taskIdInt == null) {
            println("Given task identification is not proper number")
            System.exit(1)
        }
        val taskPath = appConfiguration?.idToPathMap?.get(taskIdInt)
        if (taskPath == null) {
            println("Given path was not found in folder. Maybe you did not take number from last list command?")
            System.exit(1)
        }
        val taskMetadata = serializationService.parseMetadata(Paths.get(taskPath))
        if (taskMetadata == null) {
            println("Path $taskPath does not contain valid metadata file.")
            System.exit(1)
        }
        if (argumentAction.onlyFailed && taskMetadata!!.currentState != ExecutionMetadataState.FAILED) {
            println("Given task does not have failed status and it's state is: ${taskMetadata.currentState}")
            System.exit(1)
        }

        return taskMetadata!!
    }
}