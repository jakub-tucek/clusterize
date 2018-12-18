package cz.fit.metacentrum.service


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


    /**
     * Asks for value with type T. Value is accepted after parseValue return nonNull value.
     * @param msg - message for user
     * @param parseValue - parses value from string and returns its representation as type T, null if value is invalid
     */
    fun <T> askForValue(msg: String, parseValue: (String) -> T?): T {
        println(msg)
        while (true) {
            val input = readLine()
            // If null then according to specification, EOT (such as ctrl+d) is reached
            if (input == null) {
                println("No input given. Exiting.")
                System.exit(0)
            }
            val parsedValue = parseValue(input!!)
            if (parsedValue != null) {
                return parsedValue
            }
        }
    }

    fun askForEmail(msg: String): String {
        return askForValue(msg) { s ->
            s.takeIf { it.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()) }
        }
    }
}