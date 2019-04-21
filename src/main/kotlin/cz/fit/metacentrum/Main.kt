package cz.fit.metacentrum

import com.google.inject.Guice
import cz.fit.metacentrum.config.MainModule
import cz.fit.metacentrum.config.ProfileConfiguration
import cz.fit.metacentrum.domain.Profile
import cz.fit.metacentrum.service.MainService
import mu.KotlinLogging
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    try {
        if (ProfileConfiguration.activeProfile == Profile.PROD) {
            Configurator.setRootLevel(Level.ERROR)
        }
        logger.info {
            "Starting app. Profile: ${ProfileConfiguration.activeProfile}. Cleanup disabled: ${ProfileConfiguration.envCleanupDisabled} Arguments: ${args.joinToString(" ")}"
        }
        val injector = Guice.createInjector(MainModule())
        injector.getInstance(MainService::class.java).execute(args)

        logger.info("App finished")
    } catch (e: Throwable) {
        logger.error("Running app failed", e)
        System.err.println(e.message)
        System.exit(1)
    }
}

