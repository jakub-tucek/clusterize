package cz.fit.metacentrum.service.action.cron

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.config.appName
import cz.fit.metacentrum.service.ShellServiceImpl
import mu.KotlinLogging
import java.nio.file.Files
import javax.inject.Inject


private val logging = KotlinLogging.logger {}

/**
 *
 * @author Jakub Tucek
 */
class CronService {


    @Inject
    private lateinit var shellServiceImpl: ShellServiceImpl

    fun registerJob() {
        val cronConfig = readCronJobConfiguration()
        // find if job is already set
        val job = cronConfig.lines()
                .find { it.contentEquals(" $appName ") }
        // job found so we cant register is again
        if (job != null) {
            println("Cron job is already registered as $job")
            System.exit(1)
        }

        // append cron registration
        val newOutput = cronConfig + "\n * * * * * $appName cron-start-internal >> ${FileNames.cronLogFile} 2>&1\n"
        updateCronTab(newOutput)
    }

    fun unregisterJob() {
        val cronConfig = readCronJobConfiguration()
        val newOutput = cronConfig.lines()
                .filter { !it.contains(" $appName") }
                .joinToString("\n")
        updateCronTab(newOutput)

        println("Cron job was removed")
    }

    private fun readCronJobConfiguration(): String {
        val cronJobs = shellServiceImpl.runCommand("crontab -l")

        var output = cronJobs.output
        println(output)
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

}