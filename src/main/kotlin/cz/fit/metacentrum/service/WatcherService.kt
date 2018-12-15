package cz.fit.metacentrum.service

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.AppConfiguration
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateDone
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateFailed
import cz.fit.metacentrum.service.action.list.MetadataStatusService
import cz.fit.metacentrum.service.input.SerializationService
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
    @Inject
    private lateinit var serializationService: SerializationService
    @Inject
    private lateinit var metadataStatusService: MetadataStatusService
    @Inject
    private lateinit var consoleReader: ConsoleReader

    fun runWatcher() {
        val configPath = Paths.get(FileNames.configDataFolderName)
        killExistingWatcher(configPath)

        getFinishedTasks(configPath)
                .forEach(this::sendMail)

    }

    private fun getFinishedTasks(configPath: Path): List<ExecutionMetadata> {
        val metadataPath = configPath.resolve(FileNames.defaultMetadataFolder)


        val originalMetadata = metadataStatusService.retrieveMetadata(metadataPath)

        return originalMetadata
                .map(metadataStatusService::updateMetadataState)
                .filter { metadataStatusService.isUpdatedMetadata(originalMetadata, it) }
                .filter { it.state is ExecutionMetadataStateDone || it.state is ExecutionMetadataStateFailed }
    }

    private fun sendMail(it: ExecutionMetadata) {
        val appConfiguration = serializationService.parseAppConfiguration()
                ?: initializeAppConfiguration()
    }

    private fun initializeAppConfiguration(): AppConfiguration {
        val email = consoleReader.askForEmail("Please enter your email where notification will be sent: ")
        val appConfiguration = AppConfiguration(email)
        serializationService.persistAppConfiguration(appConfiguration)
        return appConfiguration
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