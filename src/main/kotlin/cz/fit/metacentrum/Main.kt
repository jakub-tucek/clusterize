package cz.fit.metacentrum

import cz.fit.metacentrum.service.CommandLineParser
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info("Starting app!")

    val parsedArgs = CommandLineParser().parseArguments(args)

    println(parsedArgs)
}