package cz.fit.metacentrum.service.action.cron

import cz.fit.metacentrum.config.Configuration
import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.config.ProfileConfiguration
import cz.fit.metacentrum.config.appName
import cz.fit.metacentrum.service.ShellServiceImpl
import mu.KotlinLogging
import java.nio.file.Files
import javax.inject.Inject


private val logging = KotlinLogging.logger {}

/**
 * Service that provides API for changing cron state or possibility to ask if cron is active.
 * @author Jakub Tucek
 */
class CronService {

    @Inject
    private lateinit var shellServiceImpl: ShellServiceImpl
    @Inject
    private lateinit var watcherService: WatcherService

    fun register() {
        watcherService.prepareAppConfiguration()

        val cronConfig = readCronJobConfiguration()
        // find if job is already set
        val job = retrieveRegisteredJob(cronConfig)
        // job found so we cant register is again
        if (job != null) {
            println("Cron job is already registered as $job")
            println("\nPlease first stop job before registering it again. Or run `cron restart` command.")
            System.exit(1)
        }

        // pass profile to cron job
        val envVarConfig = if (ProfileConfiguration.isDev())
            "export ${ProfileConfiguration.envVariableName}=${ProfileConfiguration.activeProfile.name} &&" else ""
        // append cron registration for every 10 minutes
        val newOutput = """$cronConfig
            |${Configuration.cronInterval} /bin/sh -c "$envVarConfig $appName cron-start-internal >> ${FileNames.cronLogFile} 2>&1"
            |""".trimMargin()
        updateCronTab(newOutput)

        println("Cron job was registered successfully")
    }

    fun unregister() {
        val cronConfig = readCronJobConfiguration()
        val newOutput = cronConfig.lines()
                .filter { !it.contains(" $appName ") }
                .joinToString("\n")
        if (newOutput == cronConfig) {
            println("Cron job cannot be removed because it is not registered.")
            return
        }
        updateCronTab(newOutput)

        println("Cron job was removed")
    }


    fun isRegistered(): Boolean {
        val config = readCronJobConfiguration()
        return retrieveRegisteredJob(config) != null
    }

    private fun readCronJobConfiguration(): String {
        val cronJobs = shellServiceImpl.runCommand("crontab -l")

        var output = cronJobs.output
        // clean output if crontab is not existing for user
        if (cronJobs.mergedOut().contains("no crontab")) {
            output = ""
        } else if (cronJobs.status != 0) {
            // status is not 1 and crontabs did not returned message about empty cron tabs
            throw IllegalStateException("Unable to retrieve existing cron jobs. ${cronJobs.mergedOut()}")
        }
        return output
    }

    private fun updateCronTab(newCronConfig: String) {
        // create temp file for rewriting cron
        val tempFile = Files.createTempFile("CronService", "configuration")
        try {
            Files.write(tempFile, newCronConfig.toByteArray())
            // edit cron configuration
            val cronSetup = shellServiceImpl.runCommand("tee < ${tempFile.toAbsolutePath()} | crontab -")
            if (cronSetup.status != 0) {
                throw IllegalStateException("Cron job was not updated properly. ${cronSetup.mergedOut()}")
            }
        } catch (e: Exception) {
            logging.error { "Unable to update cron job" }
            throw e
        } finally {
            Files.delete(tempFile)
        }
    }

    private fun retrieveRegisteredJob(cronConfig: String): String? {
        return cronConfig.lines()
                .find { it.contains(".* $appName .*".toRegex()) }
    }

}