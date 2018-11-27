package cz.fit.metacentrum.service

import cz.fit.metacentrum.config.appName
import cz.fit.metacentrum.domain.Action
import cz.fit.metacentrum.domain.ActionHelp
import cz.fit.metacentrum.domain.ActionList
import cz.fit.metacentrum.domain.ActionSubmit
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


const val genericErrorMessage = "Parsing arguments failed, value after argument expected but none was found"
const val submitCommand = "submit [path to configuration file]"
const val listCommand = "submit [options]"

/**
 * Parser of command line arguments
 */
class CommandLineParser {

    fun parseArguments(args: Array<String>): Action {
        val iterator = args.iterator()

        val nextValue = retrieveNextValue(iterator, "No parameters given. Type help for possible options")


        when (nextValue) {
            "submit" -> {
                val configFile = retrieveNextValue(iterator, "Usage: $appName $submitCommand")
                return ActionSubmit(configFile)
            }
            "help" -> {
                printHelp()
                return ActionHelp
            }
            "list" -> {
                val type = retrieveNextValue(iterator, "Usage: $appName $listCommand")
                when (type) {
                    "-p" -> return ActionList(metadataStoragePath = retrieveNextValue(iterator, "Argument expected"))
                    "-c" -> return ActionList(configFile = retrieveNextValue(iterator, "Argument expected"))
                    else -> {
                        printHelp()
                        throw IllegalArgumentException("Unrecognized flag $type")
                    }
                }
            }
            else -> {
                val msg = "Unrecognized parameter: $nextValue"
                printHelp()
                throw IllegalArgumentException(msg)
            }
        }

    }

    private fun printHelp() {
        println("""
            ====================== HELP ========================

            Usage: $appName <command> [...additional variables]

                commands:
                    $submitCommand - submits new task to clust according to configuration structure
                    help - displays help
                    $listCommand
                       -p [VALUE] - define path to metadata folder
                       -c [VALUE] - specify path to configuration file
        """.trimIndent())
    }

    private fun retrieveNextValue(iterator: Iterator<String>, msg: String = genericErrorMessage): String {
        if (!iterator.hasNext()) {
            logger.error(msg)
            throw IllegalArgumentException(msg)
        }
        return iterator.next()
    }

}