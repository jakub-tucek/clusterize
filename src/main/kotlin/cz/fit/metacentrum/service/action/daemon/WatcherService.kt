package cz.fit.metacentrum.service.action.daemon

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.config.appName
import cz.fit.metacentrum.domain.AppConfiguration
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateDone
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateFailed
import cz.fit.metacentrum.domain.template.StatusTemplateData
import cz.fit.metacentrum.service.ConsoleReader
import cz.fit.metacentrum.service.ShellServiceImpl
import cz.fit.metacentrum.service.TemplateService
import cz.fit.metacentrum.service.action.list.MetadataInfoPrinter
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
 * Watcher service that checks task state and notifies user if task finished.
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
    @Inject
    private lateinit var templateService: TemplateService
    @Inject
    private lateinit var metadataInfoPrinter: MetadataInfoPrinter

    fun checkMetadataStatus() {
        val configPath = Paths.get(FileNames.configDataFolderName)

        getFinishedTasks(configPath)
                .forEach {
                    sendMail(it)
                    // TODO: Persist metadata only here if daemon is running
                }

    }

    private fun getFinishedTasks(configPath: Path): List<ExecutionMetadata> {
        val metadataPath = configPath.resolve(FileNames.defaultMetadataFolder)


        // retrieve all metadata
        val originalMetadata = metadataStatusService.retrieveMetadata(metadataPath)

        return originalMetadata
                // check state for metadatas
                .map(metadataStatusService::updateMetadataState)
                // filter out those who were not updated in watcher
                .filter { metadataStatusService.isUpdatedMetadata(originalMetadata, it) }
                // continue only with done of finished metadata tasks
                .filter { it.state is ExecutionMetadataStateDone || it.state is ExecutionMetadataStateFailed }
    }

    private fun sendMail(it: ExecutionMetadata) {
        // get email
        val userEmail = serializationService.parseAppConfiguration()?.userEmail
                ?: initializeAppConfiguration().userEmail!! // just reinit config if email which is required is missing
        // build template data
        val templateData = buildTemplateData(it, userEmail)
        // create temp file that will be send via mail
        val tempFile = Files.createTempFile("$appName-watcher-service", "status-mail")
        try {
            templateService.write("templates/status-mail.mustache", tempFile, templateData)

            // verbose + read data (like from/to from file)
            val output = shellServiceImpl.runCommand("sendmail -tv < ${tempFile.toAbsolutePath().toString()}")
            logger.debug("Result of sending email: ${output}")
        } catch (e: Exception) {
            logger.error("Error occurred while sending email", e)
            throw e
        } finally { // clean up
            Files.delete(tempFile)
        }
    }

    private fun buildTemplateData(metadata: ExecutionMetadata, userEmail: String): StatusTemplateData {
        val state = when (metadata.state) {
            is ExecutionMetadataStateDone -> "COMPLETED"
            is ExecutionMetadataStateFailed -> "FAILED"
            else -> throw IllegalStateException("Unexpected execution state")
        }
        val body = metadataInfoPrinter.getMetadataInfo(1, metadata)
        return StatusTemplateData(
                from = "no-reply@clusterize.io",
                to = userEmail,
                subject = "Task with name ${metadata.configFile.general.taskName}/${metadata.creationTime} $state",
                resources = metadata.configFile.resources,
                creationTime = metadata.creationTime.toString(),
                updateTime = metadata.updateTime.toString(),
                taskName = metadata.configFile.general.taskName!!,
                outputPath = metadata.paths.storagePath!!,
                stateBody = body
        )
    }

    private fun initializeAppConfiguration(): AppConfiguration {
        val email = consoleReader.askForEmail("Please enter your email where notification will be sent: ")
        val appConfiguration = AppConfiguration(email)
        serializationService.persistAppConfiguration(appConfiguration)
        return appConfiguration
    }


}