package cz.fit.metacentrum.service.action.submit

import com.google.inject.name.Named
import cz.fit.metacentrum.config.actionResubmitMatlabExecutorsTokens
import cz.fit.metacentrum.domain.ActionResubmit
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.SubmitRunner
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.SerializationService
import java.nio.file.Paths
import javax.inject.Inject

/**
 * Service for resubmit failed jobs
 * @author Jakub Tucek
 */
class ActionResubmitService : ActionService<ActionResubmit> {


    @Inject
    @Named(actionResubmitMatlabExecutorsTokens)
    private lateinit var matlabResubmitExecutors: Set<@JvmSuppressWildcards TaskExecutor>
    @Inject
    private lateinit var submitRunner: SubmitRunner
    @Inject
    private lateinit var serializationService: SerializationService

    override fun processAction(argumentAction: ActionResubmit) {
        val metadata = readTaskMetadata(argumentAction)



//        val resubmitMetadata = argumentAction.metadata.copy(rerun = true)
//        when (resubmitMetadata.configFile.taskType) {
//            is MatlabTaskType -> submitRunner.run(resubmitMetadata, matlabResubmitExecutors)
//        }
    }


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

        return taskMetadata!!
    }
}