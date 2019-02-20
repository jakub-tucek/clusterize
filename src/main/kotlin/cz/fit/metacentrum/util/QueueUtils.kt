package cz.fit.metacentrum.util


/**
 * Qsub, qstat utilities
 * @author Jakub Tucek
 */
object QueueUtils {

    private val pidRegex = """^([0-9]+).*""".toRegex()

    /**
     * Pid is actually only first numbers in string. For consistency, numbers must be extracted and
     * rest of string to be thrown away.
     */
    fun extractPid(fullPid: String): String {
        if (!fullPid.matches(pidRegex)) {
            throw IllegalArgumentException("Given pid does not start with numbers: $fullPid")
        }
        val (actualPid) = pidRegex.find(fullPid)!!.destructured
        return actualPid
    }

}