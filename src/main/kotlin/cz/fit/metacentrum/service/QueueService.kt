package cz.fit.metacentrum.service

import com.google.inject.Inject
import cz.fit.metacentrum.domain.QueueRecord

/**
 * Service responsible for retrieving queue. Due to nature of application (command line interface) that
 * is restarted with each command, records is lazy loaded and instance is then returned
 * @author Jakub Tucek
 */
class QueueService {

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
        val (output, status, errOutput) = shellService.runCommand("qstat", "|", "tail", "-n", "+3")
        if (status != 0) throw IllegalStateException("Running qstat command failed with status $status. $errOutput")

        val queueRecords = output.lines()
                .map { it.replace("""\s+""".toRegex(), " ") }
                .map { parseQueueLine(it) }
                .filter { it.username.equals(username, true) }
        cache[username] = queueRecords

        return queueRecords
    }

    private fun parseQueueLine(line: String): QueueRecord {
        val lineColumns = line.split(" ")
        if (lineColumns.count() != 6)
            throw IllegalArgumentException("Parsed line has invalid count of columns (${lineColumns.count()}). \"${line}\"")

        return QueueRecord(
                lineColumns[0],
                lineColumns[1],
                lineColumns[2],
                lineColumns[3],
                lineColumns[4],
                lineColumns[5]
        )
    }
}