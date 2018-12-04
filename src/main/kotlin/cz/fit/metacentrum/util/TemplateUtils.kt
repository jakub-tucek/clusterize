package cz.fit.metacentrum.util

/**
 * Mustache template utils to substitute logic less template architecture.
 * @author Jakub Tucek
 */
object TemplateUtils {

    // formats given list as function parameters for matlab function in bash script
    fun formatFunctionParameters(data: List<String>) = data
            .map {
                // if its variable, use it directly
                // if not, wrap it in apostrophes
                if (it.startsWith("$")) it else "'${it}'"
            }
            .joinToString(", ")
}