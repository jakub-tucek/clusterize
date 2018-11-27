package cz.fit.metacentrum.util

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
}