package cz.fit.metacentrum.util

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import java.time.format.DateTimeFormatter


const val defaultCommandDelimiter = "================================================================================"

/**
 *
 * @author Jakub Tucek
 */
object ConsoleWriter {

    fun writeStatus(msg: String) {
        println(" --- $msg")
    }

    fun writeStatusDetail(msg: String) {
        println("          $msg")
    }

    fun writeStatusDone() {
        println("          DONE")
    }

    fun writeDelimiter() {
        println(defaultCommandDelimiter)
    }


    fun writeMetadas(metadatas: List<ExecutionMetadata>) {
        println(defaultCommandDelimiter)
        println("Found task executions")
        println(defaultCommandDelimiter)
        metadatas.forEachIndexed { index, executionMetadata ->
            val formattedDate = executionMetadata.timestamp.format(
                    DateTimeFormatter.ofPattern("dd/MM/YYYY hh:mm")
            )
            println("  $index - ${formattedDate}")
        }
        println(defaultCommandDelimiter)
    }
}