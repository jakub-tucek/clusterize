package cz.fit.metacentrum.service.submit

import cz.fit.metacentrum.config.matlabResubmitExecutorsToken
import cz.fit.metacentrum.domain.ActionResubmitFailed
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.SubmitRunner
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.api.TaskExecutor
import javax.inject.Inject
import javax.inject.Named

/**
 *
 * @author Jakub Tucek
 */
class ActionResubmitFailedService : ActionService<ActionResubmitFailed> {


    @Inject
    @Named(matlabResubmitExecutorsToken)
    private lateinit var matlabResubmitExecutors: Set<@JvmSuppressWildcards TaskExecutor>
    @Inject
    private lateinit var submitRunner: SubmitRunner

    override fun processAction(argumentAction: ActionResubmitFailed) {
        val resubmitMetadata = argumentAction.metadata.copy(isRerun = true)
        when (resubmitMetadata.configFile.taskType) {
            is MatlabTaskType -> submitRunner.run(resubmitMetadata, matlabResubmitExecutors)
        }
    }
}