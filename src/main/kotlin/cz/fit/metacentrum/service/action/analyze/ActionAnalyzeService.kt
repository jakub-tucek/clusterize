package cz.fit.metacentrum.service.action.analyze

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.ActionAnalyze
import cz.fit.metacentrum.domain.management.ClusterDetails
import cz.fit.metacentrum.service.action.status.QueueRecordsService
import cz.fit.metacentrum.service.api.ActionService
import cz.fit.metacentrum.service.input.SerializationService
import mu.KotlinLogging
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

private val logger = KotlinLogging.logger { }


class ActionAnalyzeService : ActionService<ActionAnalyze> {

    @Inject
    private lateinit var serializationService: SerializationService
    @Inject
    private lateinit var queueRecordsService: QueueRecordsService

    override fun processAction(argumentAction: ActionAnalyze) {
        logger.info { "Parsing cluster details" }
        val clusterDetails = serializationService.parseClusterDetails(Paths.get(argumentAction.cluterDetailsFile))

        logger.info { "Retrieving queue records " }
        val data = queueRecordsService.retrieveQueueRecords()
                .groupBy { it.queueName }
                .mapValues { it.value.size }
                .toSortedMap(String.CASE_INSENSITIVE_ORDER)

        val analysisFile = Paths.get(FileNames.analysisFile)
        val specificAnalysisFile = Paths.get(FileNames.specificAnalysisFile)
        initFile(analysisFile)
        initFile(specificAnalysisFile)
        val now = LocalDateTime.now()

        logger.info { "Writing to file" }
        try {
            writeFile(analysisFile, data, now)
        } catch (ex: IOException) {
            logger.error(ex) { "Unable to write analysis file" }
        }
        try {
            val filteredData = filterData(data, clusterDetails)
            writeFile(analysisFile, filteredData, now)
        } catch (ex: IOException) {
            logger.error(ex) { "Unable to write specific analysis file" }
        }
        logger.info { "Analysis finished " }

    }

    private fun filterData(data: SortedMap<String, Int>, clusterDetails: ClusterDetails): SortedMap<String, Int> {
        val watchedQueues = clusterDetails.queueTypes
                .flatMap { it.queues }
                .map { it.name.toLowerCase() to null }
                .toMap()
        return data.filter { watchedQueues.containsKey(it.key.toLowerCase()) }
                .toSortedMap(String.CASE_INSENSITIVE_ORDER)
    }

    private fun writeFile(analysisFile: Path, data: SortedMap<String, Int>, now: LocalDateTime) {
        val formattedData = data.map { "${it.key} - ${it.value}" }.joinToString(";")
        Files.write(analysisFile, "$now ; $formattedData".toByteArray())
    }

    private fun initFile(analysisFile: Path) {
        if (Files.exists(analysisFile)) return

        Files.createFile(analysisFile)
    }


}