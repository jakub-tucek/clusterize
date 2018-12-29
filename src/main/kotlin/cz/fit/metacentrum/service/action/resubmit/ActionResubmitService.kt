package cz.fit.metacentrum.service.action.resubmit

import com.google.inject.name.Named
import cz.fit.metacentrum.config.actionResubmitMatlabExecutorsTokens
import cz.fit.metacentrum.domain.ActionResubmit
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataHistory
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
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

        val preparedMetadata = when {
            argumentAction.onlyFailed -> resubmitFailed(metadata)
            else -> TODO("Resubmitting not failed tasks is not supported")
        }
        when (preparedMetadata.configFile.taskType) {
            is MatlabTaskType -> submitRunner.run(preparedMetadata, matlabResubmitExecutors)
        }
    }

    /**
     * Updates metadata so when executed, it will rerun only failed jobs. This is done by copying failed jobs
     * to history and setting job state to INITIAL.
     */
    private fun resubmitFailed(metadata: ExecutionMetadata): ExecutionMetadata {
        val pastJobs: MutableList<ExecutionMetadataJob> = mutableListOf()
        val jobs = metadata.jobs!!.map {
            if (it.jobInfo.state == ExecutionMetadataState.FAILED) {
                pastJobs.add(it)
                it.copy(jobInfo = it.jobInfo.copy(state = ExecutionMetadataState.INITIAL))
            } else {
                it
            }
        }
        return metadata.copy(
                jobs = jobs,
                jobsHistory = metadata.jobsHistory + ExecutionMetadataHistory(pastJobs = pastJobs)
        )
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
        if (argumentAction.onlyFailed && taskMetadata!!.currentState == ExecutionMetadataState.FAILED) {
            println("Given task does not have failed status and it's state is: ${taskMetadata.currentState}")
            System.exit(1)
        }

        return taskMetadata!!
    }
}