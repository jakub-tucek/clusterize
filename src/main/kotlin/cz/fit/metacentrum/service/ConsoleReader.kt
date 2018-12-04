package cz.fit.metacentrum.service

/**
 *
 * @author Jakub Tucek
 */
class ConsoleReader {

    fun askForConfirmation(msg: String, defaultYes: Boolean): Boolean {
        println("$msg [${if (defaultYes) "YES" else "yes"}/${if (defaultYes) "no" else "NO"}]")
        val readLine = readLine()
        if (readLine.isNullOrBlank()) {
            return defaultYes
        }
        if (readLine.equals("yes", true)) {
            return true
        }
        if (readLine.equals("no", true)) {
            return false
        }
        return defaultYes
    }


    fun <T> askForValue(msg: String, parseValue: (String) -> T?): T {
        println(msg)
        while (true) {
            val input = readLine()
            if (input.isNullOrBlank()) continue
            val parsedValue = parseValue(input)
            if (parsedValue != null) {
                return parsedValue
            }
        }
    }
}