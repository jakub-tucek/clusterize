package cz.fit.metacentrum.service.input

import cz.fit.metacentrum.config.appName
import cz.fit.metacentrum.domain.Action
import cz.fit.metacentrum.domain.ActionHelp
import cz.fit.metacentrum.domain.ActionList
import cz.fit.metacentrum.domain.ActionSubmit
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


const val genericErrorMessage = "Parsing arguments failed, value after argument expected but none was found"
const val defaultConfigPath = "${appName}-configuration.yml"

/**
 * Parser of command line arguments
 */
class CommandLineParser {

    fun parseArguments(args: Array<String>): Action {
        val iterator = args.iterator()

        val nextValue = retrieveNextValue(iterator, true)


        when (nextValue) {
            "submit" -> {
                val configFile = retrieveNextValue(iterator)
                return ActionSubmit(configFile ?: defaultConfigPath)
            }
            "help" -> {
                printHelp()
                return ActionHelp
            }
            "list" -> {
                val type = retrieveNextValue(iterator)
                when (type) {
                    "-p" -> return ActionList(metadataStoragePath = retrieveNextValue(iterator, true))
                    "-c" -> return ActionList(configFile = retrieveNextValue(iterator, true))
                    null -> return ActionList(configFile = defaultConfigPath)
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

                    submit [optional path to configuration file] - submits new task to cluster according to configuration structure
                                                                 - default path is used if not specified ($defaultConfigPath)
                    help - displays help
                    list [OPTIONS] - lists tasks
                       -p [VALUE] - define path to metadata folder
                       -c [VALUE] - specify path to configuration file or default is used ($defaultConfigPath)
        """.trimIndent())
    }

    private fun retrieveNextValue(iterator: Iterator<String>, required: Boolean = false): String? {
        if (!iterator.hasNext()) {
            if (required) {
                throw IllegalArgumentException(genericErrorMessage)
            } else {
                return null
            }
        }
        return iterator.next()
    }

}