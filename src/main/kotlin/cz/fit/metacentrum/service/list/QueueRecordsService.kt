package cz.fit.metacentrum.service.list

import com.google.inject.Inject
import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.service.ShellService
import cz.fit.metacentrum.util.QueueUtils

/**
 * Service responsible for retrieving queue. Due to nature of application (command line interface) that
 * is restarted with each command, records is lazy loaded and instance is then returned
 * @author Jakub Tucek
 */
class QueueRecordsService {

    @Inject
    private lateinit var shellService: ShellService


    // cache of records for username
    private var cache: MutableMap<String, List<QueueRecord>> = mutableMapOf()

    fun retrieveQueueForUser(username: String): List<QueueRecord> {
        val cachedResult = cache.get(username)
        if (cachedResult != null) {
            return cachedResult
        }


        // run qstat command and ship useless lines
        val (output, status, errOutput) = shellService.runCommand("ls | grep $username")
        if (status != 0) throw IllegalStateException("Running qstat command failed with status $status. $errOutput")

        val queueRecords = output.lines()
                .map { it.replace("""\s+""".toRegex(), " ").trim() }
                .filter { it.isNotBlank() }
                .map { parseQueueLine(it) }
                .filter { it.username.equals(username, true) }
        cache[username] = queueRecords

        return queueRecords
    }

    private fun parseQueueLine(line: String): QueueRecord {
        val lineColumns = line
                // merge job id so it does not fuck up parsing
                .replace("Job id", "JobId")
                .split(" ")
        if (lineColumns.count() != 6)
            throw IllegalArgumentException("Parsed line has invalid count of columns (${lineColumns.count()}). \"${line}\"")

        // retrieve internal state value
        val internalState = QueueRecord.InternalState.valueOf(lineColumns[4])
        // map internal state representation to state
        val state = when (internalState) {
            QueueRecord.InternalState.Q -> QueueRecord.State.QUEUED
            else -> QueueRecord.State.RUNNING
        }
        return QueueRecord(
                QueueUtils.extractPid(lineColumns[0]),
                lineColumns[1],
                lineColumns[2],
                lineColumns[3],
                internalState,
                state,
                lineColumns[5]
        )
    }
}