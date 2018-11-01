package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.ArgumentInput
import mu.KotlinLogging
import java.lang.IllegalArgumentException

private val logger = KotlinLogging.logger {}

/**
 * Parser of command line arguments
 */
class CommandLineParser {

    fun parseArguments(args: Array<String>): ArgumentInput {
        val iterator = args.iterator()

        var inputFile = ""

        while (iterator.hasNext()) {
            val value = iterator.next()
            when (value) {
                "-i", "--i" -> inputFile = retrieveNextValue(iterator)
            }
        }


        return ArgumentInput(
                inputFile
        )
    }

    private fun retrieveNextValue(iterator: Iterator<String>): String {
        if (!iterator.hasNext()) {
            val msg = "Parsing arguments failed, value after argument expected but none was found"
            logger.error(msg)
            throw IllegalArgumentException(msg)
        }
        return iterator.next()
    }
}