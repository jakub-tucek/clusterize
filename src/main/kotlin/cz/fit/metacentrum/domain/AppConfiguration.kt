package cz.fit.metacentrum.domain

/**
 * Holder for global app configuration, such as user email where notification is send.
 * @author Jakub Tucek
 */
data class AppConfiguration(
        val userEmail: String?
)