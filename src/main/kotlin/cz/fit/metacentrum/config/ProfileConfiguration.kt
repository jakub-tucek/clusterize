package cz.fit.metacentrum.config

import cz.fit.metacentrum.domain.Profile

/**
 *
 * @author Jakub Tucek
 */
object ProfileConfiguration {


    val activeProfile: Profile by lazy {
        val profile = System.getenv("${appName.toUpperCase()}_PROFILE").toLowerCase()
        when (profile) {
            "dev" -> Profile.DEV
            else -> Profile.PROD
        }
    }

    fun isDev(): Boolean {
        return activeProfile == Profile.DEV
    }


}