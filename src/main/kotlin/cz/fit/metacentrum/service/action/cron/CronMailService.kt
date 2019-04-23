package cz.fit.metacentrum.service.action.cron

import cz.fit.metacentrum.config.appName
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.domain.template.StatusMailTemplateData
import cz.fit.metacentrum.service.ShellServiceImpl
import cz.fit.metacentrum.service.TemplateService
import cz.fit.metacentrum.service.action.status.MetadataInfoPrinter
import cz.fit.metacentrum.service.input.SerializationService
import mu.KotlinLogging
import java.nio.file.Files
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

/**
 *
 * @author Jakub Tucek
 */
class CronMailService {
    @Inject
    private lateinit var shellServiceImpl: ShellServiceImpl
    @Inject
    private lateinit var serializationService: SerializationService
    @Inject
    private lateinit var templateService: TemplateService
    @Inject
    private lateinit var metadataInfoPrinter: MetadataInfoPrinter

    fun sendMail(it: ExecutionMetadata) {
        // get email
        val userEmail = serializationService.parseAppConfiguration()?.userEmail
                ?: throw java.lang.IllegalStateException("User email is missing in configuration")
        // build template data
        val templateData = buildTemplateData(it, userEmail)
        // create temp file that will be send via mail
        val tempFile = Files.createTempFile("$appName-watcher-service", "status-mail")
        try {
            templateService.write("templates/status-mail.mustache", tempFile, templateData)

            // verbose + read data (like from/to from file)
            val output = shellServiceImpl.runCommand("/usr/sbin/sendmail -tv < ${tempFile.toAbsolutePath()}")
            logger.info("Mail send for ${templateData.subject} with status ${output.status}")
        } catch (e: Exception) {
            logger.error("Error occurred while sending email", e)
            throw e
        } finally { // clean up
            Files.delete(tempFile)
        }
    }

    private fun buildTemplateData(metadata: ExecutionMetadata, userEmail: String): StatusMailTemplateData {
        val state = when (metadata.currentState) {
            ExecutionMetadataState.DONE -> "COMPLETED"
            ExecutionMetadataState.FAILED -> "FAILED"
            else -> throw IllegalStateException("Unexpected execution state")
        }
        val body = metadataInfoPrinter.getMetadataInfo(1, metadata) + metadataInfoPrinter.printMetadataHistory(metadata)

        return StatusMailTemplateData(
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


}