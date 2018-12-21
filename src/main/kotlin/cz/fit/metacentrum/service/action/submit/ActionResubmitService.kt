package cz.fit.metacentrum.service.action.submit

import com.google.inject.name.Named
import cz.fit.metacentrum.config.actionResubmitMatlabExecutorsTokens
import cz.fit.metacentrum.domain.ActionResubmit
import cz.fit.metacentrum.service.SubmitRunner
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.api.TaskExecutor
import cz.fit.metacentrum.service.input.SerializationService
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
        val (taskId, metadataStoragePath) = argumentAction
//        val resubmitMetadata = argumentAction.metadata.copy(rerun = true)
//        when (resubmitMetadata.configFile.taskType) {
//            is MatlabTaskType -> submitRunner.run(resubmitMetadata, matlabResubmitExecutors)
//        }
    }
}