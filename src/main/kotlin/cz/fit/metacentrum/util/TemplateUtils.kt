package cz.fit.metacentrum.util

/**
 *
 * @author Jakub Tucek
 */
object TemplateUtils {

    fun formatFunctionParameters(data: List<String>) = data
            .map {
                if (it.startsWith("$")) it else "'${it}'"
            }
            .joinToString(", ")
}