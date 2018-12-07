package cz.fit.metacentrum.service.list

import cz.fit.metacentrum.domain.ActionSubmit
import cz.fit.metacentrum.domain.ActionSubmitConfig
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateFailed
import cz.fit.metacentrum.service.ConsoleReader
import cz.fit.metacentrum.service.api.ActionService
import javax.inject.Inject

/**
 * Resubmitting configuration file.
 * @author Jakub Tucek
 */
class TaskResubmitService() {

    @Inject
    private lateinit var actionSubmitService: ActionService<ActionSubmit>
    @Inject
    private lateinit var consoleReader: ConsoleReader

    fun promptRerunIfError(metadatas: List<ExecutionMetadata>) {
        val failedTasks = metadatas
                .filter { it.state != null && it.state is ExecutionMetadataStateFailed }
        // check if some failed tasks are present, in yes, prompt resubmit task
        if (failedTasks.isEmpty()) {
            return
        }

        resubmit(metadatas, readResubmitId {
            parseValidId(it, metadatas.size)
        });
    }

    private fun readResubmitId(idParser: (String?) -> Int?): Int {
        return consoleReader.askForValue(
                "Looks like you have some failed tasks. Do you want to submit some task again? [ENTER TASK NUMBER]"
        ) { s -> idParser(s) }
    }

    private fun parseValidId(idInput: String?, maxSize: Int): Int? {
        val id = idInput?.toIntOrNull()
        if (id == null
                || id < 0
                || id >= maxSize) {
            return null
        }
        return id
    }

    /**
     * Resubmits for given rerun id if given.
     */
    fun resubmit(metadatas: List<ExecutionMetadata>, resubmitId: Int) {
        val metadata = metadatas[resubmitId]

        // Update configuration resources path so it directs on copied metadata files
        val config = metadata.configFile.copy(general = metadata.configFile.general
                .copy(sourcesPath = metadata.paths.sourcesPath.toString())
        )

        actionSubmitService.processAction(ActionSubmitConfig(config))
    }

    fun resubmit(metadatas: List<ExecutionMetadata>, resubmitIdString: String) {
        val parsedId = parseValidId(resubmitIdString, metadatas.size)
        if (parsedId != null) {
            resubmit(metadatas, parsedId)
        }
        println("Given task id is invalid. Existing.")
        System.exit(1)
    }
}