package cz.fit.metacentrum.config

import cz.fit.metacentrum.domain.Profile

/**
 *
 * @author Jakub Tucek
 */
object ProfileConfiguration {

    val envProfileName = "${appName.toUpperCase()}_PROFILE"
    val envCleanupDisabledName = "${appName.toUpperCase()}_DISABLE_CLEANUP"

    val activeProfile: Profile by lazy {
        val profile = System.getenv(envProfileName)?.toLowerCase()
        when (profile) {
            "dev" -> Profile.DEV
            else -> Profile.PROD
        }
    }

    fun isDev(): Boolean {
        return activeProfile == Profile.DEV
    }

    val envCleanupDisabled: Boolean by lazy {
        System.getenv(envCleanupDisabledName)?.toBoolean() ?: false
    }
}