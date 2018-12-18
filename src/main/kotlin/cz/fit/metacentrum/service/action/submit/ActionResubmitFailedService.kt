package cz.fit.metacentrum.service.action.submit

import com.google.inject.name.Named
import cz.fit.metacentrum.config.actionResubmitMatlabExecutorsTokens
import cz.fit.metacentrum.domain.ActionResubmitFailed
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.SubmitRunner
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.api.TaskExecutor
import javax.inject.Inject

/**
 * Service for resubmit failed jobs
 * @author Jakub Tucek
 */
class ActionResubmitFailedService : ActionService<ActionResubmitFailed> {


    @Inject
    @Named(actionResubmitMatlabExecutorsTokens)
    private lateinit var matlabResubmitExecutors: Set<@JvmSuppressWildcards TaskExecutor>
    @Inject
    private lateinit var submitRunner: SubmitRunner

    override fun processAction(argumentAction: ActionResubmitFailed) {
        val resubmitMetadata = argumentAction.metadata.copy(rerun = true)
        when (resubmitMetadata.configFile.taskType) {
            is MatlabTaskType -> submitRunner.run(resubmitMetadata, matlabResubmitExecutors)
        }
    }
}