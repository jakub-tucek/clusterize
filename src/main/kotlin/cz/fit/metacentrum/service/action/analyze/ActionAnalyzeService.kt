package cz.fit.metacentrum.service.action.analyze

import cz.fit.metacentrum.domain.ActionAnalyze
import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.management.ClusterDetails
import cz.fit.metacentrum.service.action.status.QueueRecordsService
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.input.SerializationService
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import javax.inject.Inject

private val logger = KotlinLogging.logger { }


private const val SEPARATOR = " ; "

class ActionAnalyzeService : ActionService<ActionAnalyze> {

    @Inject
    private lateinit var serializationService: SerializationService
    @Inject
    private lateinit var queueRecordsService: QueueRecordsService
    @Inject
    private lateinit var actionAnalyzePathProvider: ActionAnalyzePathProvider

    override fun processAction(argumentAction: ActionAnalyze) {
        logger.info { "Parsing cluster details" }
        val clusterDetails = serializationService.parseClusterDetails(Paths.get(argumentAction.cluterDetailsFile))

        logger.info { "Retrieving queue records " }
        val records = queueRecordsService.retrieveQueueRecords()
        logger.info { "Preparing data" }
        val now = LocalDateTime.now()
        writeAllData(records, now)

        writeWatchedQueues(records, clusterDetails, now)
    }

    private fun writeWatchedQueues(records: List<QueueRecord>, clusterDetails: ClusterDetails, now: LocalDateTime) {
        val watchedQueueNames = clusterDetails.queueTypes
                .flatMap { it.queues }
                .map { it.name }
                .sortedWith(String.CASE_INSENSITIVE_ORDER)
        val watchedQueues = watchedQueueNames
                .map { it to 0 }
                .toMap()
                .toSortedMap(String.CASE_INSENSITIVE_ORDER)
        records.forEach {
            val value = watchedQueues.get(it.queueName)
            if (value != null) {
                watchedQueues[it.queueName] = value + 1
            }
        }

        val file = actionAnalyzePathProvider.retrieveSpecificAnalysisPath()
        if (Files.notExists(file)) {
            val queueNamesHeader = watchedQueueNames.joinToString(SEPARATOR)
            Files.write(file, "timestamp$SEPARATOR$queueNamesHeader\n".toByteArray(), StandardOpenOption.CREATE_NEW)
        }
        val data = watchedQueues.values.joinToString(SEPARATOR)
        appendDataToFile(file, data, now)
    }

    private fun writeAllData(records: List<QueueRecord>, now: LocalDateTime) {
        val data = records.groupBy { it.queueName }
                .mapValues { it.value.size }
                .toSortedMap(String.CASE_INSENSITIVE_ORDER)

        val analysisFile = actionAnalyzePathProvider.retrieveAnalysisPath()
        initFile(analysisFile)

        try {
            val formattedData = data.map { "${it.key} - ${it.value}" }.joinToString(SEPARATOR)
            appendDataToFile(analysisFile, formattedData, now)
        } catch (ex: IOException) {
            logger.error(ex) { "Unable to write analysis file" }
        }
    }

    private fun appendDataToFile(analysisFile: Path, formattedData: String, now: LocalDateTime) {
        Files.write(analysisFile, "$now$SEPARATOR$formattedData\n".toByteArray(), StandardOpenOption.APPEND)
    }

    private fun initFile(analysisFile: Path) {
        if (Files.exists(analysisFile)) return

        Files.createFile(analysisFile)
    }


}