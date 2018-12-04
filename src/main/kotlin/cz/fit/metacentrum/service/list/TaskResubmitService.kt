package cz.fit.metacentrum.service.list

import com.google.inject.Inject
import cz.fit.metacentrum.domain.ActionSubmit
import cz.fit.metacentrum.domain.ActionSubmitConfig
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateFailed
import cz.fit.metacentrum.service.api.ActionService

/**
 * Resubmitting configuration file.
 * @author Jakub Tucek
 */
class TaskResubmitService {

    @Inject
    private lateinit var actionSubmitService: ActionService<ActionSubmit>

    fun promptRerunIfError(metadatas: List<ExecutionMetadata>) {
        val failedTasks = metadatas
                .filter { it.state != null && it.state is ExecutionMetadataStateFailed }
        // check if some failed tasks are present, in yes, prompt resubmit task
        if (failedTasks.isEmpty()) {
            return
        }

        resubmit(metadatas)
    }


    /**
     * Resubmits for given rerun id if given.
     */
    fun resubmit(metadatas: List<ExecutionMetadata>, defaultResubmitId: String? = null) {
        var resubmitId = defaultResubmitId
        if (resubmitId == null) {
            println("Looks like you have some failed tasks. Do you want to submit some task again? [ENTER TASK NUMBER]")
            resubmitId = readLine() ?: return
        }

        val resubmitIdInt = resubmitId.toIntOrNull()
        if (resubmitIdInt == null
                || resubmitIdInt < 0
                || resubmitIdInt >= metadatas.size) {
            println("No viable input found. Exiting.")
            return
        }
        val metadata = metadatas[resubmitIdInt]


        // Update configuration resources path so it directs on copied metadata files
        val config = metadata.configFile
                .copy(general = metadata.configFile.general
                        .copy(sourcesPath = metadata.paths.sourcesPath.toString()))

        actionSubmitService.processAction(ActionSubmitConfig(config))

    }
}