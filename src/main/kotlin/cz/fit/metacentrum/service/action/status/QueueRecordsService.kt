package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.service.api.ShellService
import cz.fit.metacentrum.util.QueueUtils
import mu.KotlinLogging
import javax.inject.Inject


private val logger = KotlinLogging.logger { }

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


    fun retrieveQueueRecords(): List<QueueRecord> {
        val (output, status, errOutput) = shellService.runCommand("export PBSPRO_IGNORE_KERBEROS=yes && qstat -i")
        if (status != 0) throw IllegalStateException("Running qstat command failed with status $status. $errOutput")
        logger.debug { "Converting records to objects" }
        return convertOutputToRecords(output)
    }

    fun retrieveQueueForUser(username: String): List<QueueRecord> {
        val cachedResult = cache.get(username)
        if (cachedResult != null) {
            return cachedResult
        }

        // run qstat command
        val (output, status, errOutput) = shellService.runCommand("export PBSPRO_IGNORE_KERBEROS=yes && qstat -u $username")
        if (status != 0) throw IllegalStateException("Running qstat command failed with status $status. $errOutput")

        val queueRecords = convertOutputToRecords(output)
        cache[username] = queueRecords

        return queueRecords
    }

    private fun convertOutputToRecords(output: String): List<QueueRecord> {
        return output.lines()
                .drop(4)
                .map { it.replace("""\s+""".toRegex(), " ").trim() }
                .filter { it.isNotBlank() }
                .map { parseQueueLine(it) }
    }

    private fun parseQueueLine(line: String): QueueRecord {
        val lineColumns = line
                .split(" ")
        if (lineColumns.count() != 11)
            throw IllegalArgumentException("Parsed line has invalid count of columns (${lineColumns.count()}). \"${line}\"")

        // retrieve internal state value
        val internalState = when {
            lineColumns[9] != "-" -> QueueRecord.InternalState.valueOf(lineColumns[9])
            else -> QueueRecord.InternalState.U
        }
        // map internal state representation to state
        val state = when (internalState) {
            QueueRecord.InternalState.Q -> QueueRecord.State.QUEUED
            else -> QueueRecord.State.RUNNING
        }
        return QueueRecord(
                pid = QueueUtils.extractPid(lineColumns[0]),
                username = lineColumns[1],
                queueName = lineColumns[2],
                jobName = lineColumns[3],
                sessionId = lineColumns[4],
                nds = lineColumns[5],
                tsk = lineColumns[6],
                requiredMemory = lineColumns[7],
                requiredTime = lineColumns[8],
                internalState = internalState,
                elapsedTime = lineColumns[10],
                state = state
        )
    }

}