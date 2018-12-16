package cz.fit.metacentrum.service.action.daemon

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.config.appName
import cz.fit.metacentrum.service.ShellServiceImpl
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import javax.inject.Inject


private val logger = KotlinLogging.logger {}
private const val watcherPIDFileName = "watcher-pid"


/**
 *
 * @author Jakub Tucek
 */
class DaemonService {

    @Inject
    private lateinit var shellServiceImpl: ShellServiceImpl

    fun killDaemon() {
        val pid = retrieveWatcherPid()
        if (pid == null) {
            logger.info("Pid not present")
            return
        }
        logger.info("Killing existing watcher with pid $pid")
        try {
            shellServiceImpl.runCommand("kill -2 $pid")
            println("Daemon was successfully stopped")
        } catch (e: InterruptedException) {
            logger.info("Killing with code -2 (SIGINT) timeouted", e)
            logger.info("Killing with Using SIGTERM")
            shellServiceImpl.runCommand("kill -9 $pid")
            println("Daemon was successfully stopped by force")
        }
    }

    fun execDaemon() {
        if (isDaemonRunning()) {
            println("Daemon is already running.")
            System.exit(1)
        }
        logger.info("Executing daemon on background")

        val pid = shellServiceImpl.runCommandAsync(
                "nohup $appName daemon-start-internal > ${FileNames.daemonLogFile} 2>&1"
        )
        println("Daemon was executed under PID $pid")

        val watcherPID = Paths.get(FileNames.configDataFolderName).resolve(watcherPIDFileName)
        Files.write(watcherPID, pid.toString().toByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }

    fun isDaemonRunning(): Boolean {
        val pid = retrieveWatcherPid()
        if (pid.isNullOrBlank()) {
            return false
        }
        val output = shellServiceImpl.runCommand("ps -p $pid")
        // if 0 command process exists (running), 1 if does not
        return output.status == 0
    }

    /**
     * Returns watcher pid from configuration path. If not present, null is returned.
     */
    private fun retrieveWatcherPid(): String? {
        val watcherPID = Paths.get(FileNames.configDataFolderName).resolve(watcherPIDFileName)
        if (Files.notExists(watcherPID)) return null

        val pid = Files.readAllLines(watcherPID).firstOrNull()?.ifBlank { null }
        return pid
    }

}