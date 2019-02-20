package cz.fit.metacentrum.service.input

import cz.fit.metacentrum.config.FileNames.defaultMetadataFolder
import cz.fit.metacentrum.config.FileNames.defaultSourceFile
import cz.fit.metacentrum.config.appName
import cz.fit.metacentrum.domain.*
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

        val nextValue = retrieveNextValue(iterator, false)

        when (nextValue) {
            "submit" -> {
                val configFile = retrieveNextValue(iterator)
                return ActionSubmitPath(configFile ?: defaultConfigPath)
            }
            "help" -> {
                printHelp()
                return ActionHelp
            }
            "list" -> {
                val type = retrieveNextValue(iterator)
                return when (type) {
                    "-p" -> ActionStatus(metadataStoragePath = retrieveNextValue(iterator, true))
                    "-c" -> ActionStatus(configFile = retrieveNextValue(iterator, true))
                    null -> ActionStatus(metadataStoragePath = defaultMetadataFolder)
                    else -> {
                        printHelp()
                        throw IllegalArgumentException("Unrecognized flag $type")
                    }
                }
            }
            "resubmit" -> {
                val taskId = retrieveNextValue(iterator, true)!!
                return ActionResubmit(taskId = taskId, metadataStoragePath = defaultMetadataFolder)
            }
            "cron" -> {
                val cronCommandType = retrieveNextValue(iterator, true)!!
                return when (cronCommandType) {
                    "start" -> ActionCron(ActionCron.Type.START)
                    "stop" -> ActionCron(ActionCron.Type.STOP)
                    "restart" -> ActionCron(ActionCron.Type.RESTART)
                    else -> {
                        printHelp()
                        throw IllegalArgumentException("Unrecognized option")
                    }
                }
            }
            "cron-start-internal" -> return ActionCronStartInternal
            "analyze" -> {
                val sourceFilePath = retrieveNextValue(iterator, false) ?: defaultSourceFile
                return ActionAnalyze(sourceFilePath)
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
                    resubmit [task id] - resubmits failed jobs of given task
                    list [OPTIONS] - lists tasks
                       -p [VALUE] - define path to metadata folder or default is used
                       -c [VALUE] - specify path to configuration file
                    cron [start|stop] - run cron to watch executed tasks and receive notifications
                    analyze [optional clusterInfoFile] - analyzes cluster
                    help - displays help
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