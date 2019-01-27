package cz.fit.metacentrum.service.action.cron

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.AppConfiguration
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.ConsoleReader
import cz.fit.metacentrum.service.action.resubmit.ResubmitService
import cz.fit.metacentrum.service.action.status.MetadataInfoPrinter
import cz.fit.metacentrum.service.action.status.MetadataStatusService
import cz.fit.metacentrum.service.input.SerializationService
import java.nio.file.Path
import java.nio.file.Paths
import javax.inject.Inject


private const val watcherPIDFileName = "watcher-pid"

/**
 * Watcher service that checks task state and notifies user if task finished.
 * @author Jakub Tucek
 */
class WatcherService {

    @Inject
    private lateinit var serializationService: SerializationService
    @Inject
    private lateinit var metadataStatusService: MetadataStatusService
    @Inject
    private lateinit var consoleReader: ConsoleReader
    @Inject
    private lateinit var metadataInfoPrinter: MetadataInfoPrinter
    @Inject
    private lateinit var resubmitService: ResubmitService
    @Inject
    private lateinit var cronMailService: CronMailService

    fun prepareAppConfiguration() {
        serializationService.parseAppConfiguration() ?: initializeAppConfiguration()
    }

    fun checkMetadataStatus() {
        val configPath = Paths.get(FileNames.configDataFolderName)

        val updatedTasks = getUpdatedTasks(configPath)
        updatedTasks.forEachIndexed { index, metadata ->

            if (!resubmitService.checkJobsForResubmit(metadata)) {
                metadataInfoPrinter.printMetadataInfo(index, metadata)
                cronMailService.sendMail(metadata)
                serializationService.persistMetadata(metadata)
            }
        }
    }

    private fun getUpdatedTasks(configPath: Path): List<ExecutionMetadata> {
        val metadataPath = configPath.resolve(FileNames.defaultMetadataFolder)


        // retrieve all metadata
        val originalMetadata = metadataStatusService.retrieveMetadata(metadataPath)

        return originalMetadata
                // check state of metadatas
                .map(metadataStatusService::updateMetadataState)
                // filter out those who were not updated in watcher
                .filter { metadataStatusService.isUpdatedMetadata(originalMetadata, it) }
    }

    private fun initializeAppConfiguration(): AppConfiguration {
        val email = consoleReader.askForEmail("Please enter your email where notification will be sent: ")
        val appConfiguration = AppConfiguration(email)
        serializationService.persistAppConfiguration(appConfiguration)
        return appConfiguration
    }
}