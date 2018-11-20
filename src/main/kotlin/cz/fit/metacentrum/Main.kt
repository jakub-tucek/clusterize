package cz.fit.metacentrum

import cz.fit.metacentrum.service.CommandLineParser
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    fun doExecute(args: Array<String>) {
        logger.info("Starting app!")

        val parsedArgs = CommandLineParser().parseArguments(args)

        println(parsedArgs)
    }

    try {
        doExecute(args)
    } catch (e: Throwable) {
        logger.info("Running app failed", e)
        System.err.println(e.message)
        System.exit(1)
    }


}

