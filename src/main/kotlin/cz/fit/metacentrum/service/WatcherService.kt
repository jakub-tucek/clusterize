package cz.fit.metacentrum.service

import cz.fit.metacentrum.config.FileNames.configDataFolderName
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject


private const val watcherPIDFileName = "watcher-pid"

private val logger = KotlinLogging.logger {}

/**
 *
 * @author Jakub Tucek
 */
class WatcherService {

    @Inject
    private lateinit var shellServiceImpl: ShellServiceImpl

    fun runWatcher() {
        val configPath = Paths.get(configDataFolderName)
        killExistingWatcher(configPath)

    }

    private fun killExistingWatcher(configPath: Path) {
        val pid = retrieveWatcherPid(configPath)
        if (pid == null) {
            logger.debug("Pid not present")
            return
        }
        logger.debug("Killing existing watcher with pid $pid")
        shellServiceImpl.runCommand("kill -9 $pid")
    }

    /**
     * Returns watcher pid from configuration path. If not present, null is returned.
     */
    private fun retrieveWatcherPid(configPath: Path): String? {
        val watcherPID = configPath.resolve(watcherPIDFileName)
        if (Files.notExists(watcherPID)) return null

        val pid = Files.readAllLines(watcherPID).firstOrNull()?.ifBlank { null }
        return pid
    }


}