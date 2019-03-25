package cz.fit.metacentrum.util


const val defaultCommandDelimiter = "================================================================================"

/**
 * Console writer of status and information for user to have consistent spacing
 * @author Jakub Tucek
 */
object ConsoleWriter {

    fun writeHeader(head: String) {
        println("     $head")
    }

    fun writeStatus(msg: String) {
        println(" --- $msg")
    }

    fun writeListItem(msg: String) {
        println("   * $msg")
    }

    fun writeStatusDetail(msg: String, newline: Boolean = true) {
        if (newline) {
            println(getStatusDetailLine(msg))
        } else {
            print(getStatusDetailLine(msg))
        }
    }

    fun deleteStatusDetail(lastMsg: String) {
        val deleteStr = "\u0008".repeat(getStatusDetailLine(lastMsg).length)
        print(deleteStr)
    }

    fun getStatusDetailLine(msg: String): String {
        return "          $msg"
    }

    fun writeStatusDone() {
        println("          DONE")
    }

    fun writeDelimiter() {
        println(defaultCommandDelimiter)
    }
}