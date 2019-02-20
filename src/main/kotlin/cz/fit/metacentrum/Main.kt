package cz.fit.metacentrum

import com.google.inject.Guice
import cz.fit.metacentrum.config.MainModule
import cz.fit.metacentrum.config.ProfileConfiguration
import cz.fit.metacentrum.service.MainService
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    try {
        logger.info {
            "Starting app. Profile: ${ProfileConfiguration.activeProfile} Arguments: ${args.joinToString(" ")}"
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

