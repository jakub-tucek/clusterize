package cz.fit.metacentrum.service.list

import com.google.inject.Inject
import cz.fit.metacentrum.domain.ActionSubmit
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateFailed
import cz.fit.metacentrum.service.api.ActionService

/**
 *
 * @author Jakub Tucek
 */
class TaskResubmitService {

    @Inject
    private lateinit var actionSubmitService: ActionService<ActionSubmit>

    fun promptRerunIfError(metadatas: List<ExecutionMetadata>) {
        val failedTasks = metadatas
                // find handle state
                .filter { it.state != null && it.state is ExecutionMetadataStateFailed }
        if (failedTasks.isEmpty()) {
            return
        }


        submitTask(metadatas)
    }


    fun submitTask(metadatas: List<ExecutionMetadata>, userRerunId: String? = null) {
        var userRerun = userRerunId
        if (userRerun == null) {
            println("Looks like you have some failed tasks. Do you want to submit some task again? [ENTER TASK NUMBER]")
            userRerun = readLine() ?: return
        }

        val userInputInt = userRerun.toIntOrNull()
        if (userInputInt == null
                || userInputInt < 0
                || userInputInt >= metadatas.size) {
            println("Invalid number given. Exiting.")
            return
        }
        val metadata = metadatas[userInputInt]
        actionSubmitService.processAction(
                ActionSubmit(
                        metadata.configFile.toString() // TODO fix
                )
        )

    }
}