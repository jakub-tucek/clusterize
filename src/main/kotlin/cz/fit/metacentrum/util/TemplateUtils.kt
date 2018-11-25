package cz.fit.metacentrum.util

/**
 *
 * @author Jakub Tucek
 */
object TemplateUtils {

    fun formatAsFunctionParams(data: List<String>) = data.map { "$" + it }
            .joinToString(", ")
}