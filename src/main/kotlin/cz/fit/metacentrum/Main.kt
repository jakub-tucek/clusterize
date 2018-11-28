package cz.fit.metacentrum

import com.google.inject.Guice
import cz.fit.metacentrum.config.MainModule
import cz.fit.metacentrum.service.MainService
import mu.KotlinLogging


private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    try {
        logger.debug("Starting app!")

        val injector = Guice.createInjector(MainModule())
        injector.getInstance(MainService::class.java).execute(args)

        logger.debug("App finished without unexpected error")
    } catch (e: Throwable) {
        logger.error("Running app failed", e)
        System.err.println(e.message)
        System.exit(1)
    }
}

