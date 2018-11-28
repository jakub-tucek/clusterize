package cz.fit.metacentrum.util

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import java.time.format.DateTimeFormatter


private const val defaultListDelimeter = "================================================================================"

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

    fun writeExecutorsRunEnd() {
        println(defaultListDelimeter)
    }

    fun writeMetadas(metadatas: List<ExecutionMetadata>) {
        println(defaultListDelimeter)
        println("Found task executions")
        println(defaultListDelimeter)
        metadatas.forEachIndexed { index, executionMetadata ->
            val formattedDate = executionMetadata.timestamp.format(
                    DateTimeFormatter.ofPattern("dd/MM/YYYY hh:mm")
            )
            println("  $index - ${formattedDate}")
        }
        println(defaultListDelimeter)
    }
}