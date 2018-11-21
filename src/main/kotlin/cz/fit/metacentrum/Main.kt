package cz.fit.metacentrum

import com.google.inject.Guice
import cz.fit.metacentrum.service.MetacentrumCliService
import mu.KotlinLogging


private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    try {
        logger.debug("Starting app!")

        val injector = Guice.createInjector(MetacentrumModule())
        injector.getInstance(MetacentrumCliService::class.java).execute(args)

        logger.debug("App finished without unexpected error")
    } catch (e: Throwable) {
        logger.info("Running app failed", e)
        System.err.println(e.message)
        System.exit(1)
    }
}

