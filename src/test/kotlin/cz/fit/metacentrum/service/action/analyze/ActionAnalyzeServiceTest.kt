package cz.fit.metacentrum.service.action.analyze

import com.google.common.jimfs.Jimfs
import cz.fit.metacentrum.KotlinMockito
import cz.fit.metacentrum.TestData
import cz.fit.metacentrum.domain.ActionAnalyze
import cz.fit.metacentrum.domain.management.ClusterDetails
import cz.fit.metacentrum.domain.management.ClusterQueue
import cz.fit.metacentrum.domain.management.ClusterQueueType
import cz.fit.metacentrum.service.action.status.QueueRecordsService
import cz.fit.metacentrum.service.input.SerializationService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Jakub Tucek
 */
@ExtendWith(MockitoExtension::class)
internal class ActionAnalyzeServiceTest {

    @InjectMocks
    private lateinit var service: ActionAnalyzeService

    @Mock
    private lateinit var serializationService: SerializationService
    @Mock
    private lateinit var queueRecordsService: QueueRecordsService
    @Mock
    private lateinit var specificActionAnalyzePathProvider: ActionAnalyzePathProvider


    private lateinit var analysisPath: Path
    private lateinit var specificAnalysisPath: Path

    @BeforeEach
    fun setUp() {
        Mockito.`when`(serializationService.parseClusterDetails(KotlinMockito.any()))
                .thenReturn(ClusterDetails(
                        "",
                        listOf(
                                ClusterQueueType(
                                        "cpu",
                                        listOf(
                                                ClusterQueue(
                                                        "q1",
                                                        "",
                                                        "",
                                                        maxCPU = "10"
                                                ),
                                                ClusterQueue(
                                                        "q2",
                                                        "",
                                                        "",
                                                        maxCPU = "10"
                                                )
                                        )
                                )
                        )
                ))
        Mockito.`when`(queueRecordsService.retrieveQueueRecords()).thenReturn(
                listOf(
                        TestData.queueRecordRunning.copy(queueName = "q1"),
                        TestData.queueRecordRunning.copy(queueName = "q1"),
                        TestData.queueRecordRunning.copy(queueName = "q1"),
                        TestData.queueRecordRunning.copy(queueName = "q1"),
                        TestData.queueRecordRunning.copy(queueName = "q_unknown")
                )
        )
        val fs = Jimfs.newFileSystem()
        analysisPath = fs.getPath("/analysis")
        specificAnalysisPath = fs.getPath("/specificAnalysis")
        Mockito.`when`(specificActionAnalyzePathProvider.retrieveAnalysisPath()).thenReturn(analysisPath)
        Mockito.`when`(specificActionAnalyzePathProvider.retrieveSpecificAnalysisPath()).thenReturn(specificAnalysisPath)
    }

    @Test
    fun testThatWholeAnalysisIsWrittenToFile() {
        service.processAction(ActionAnalyze(""))


        val result = Files.readAllLines(analysisPath)
        Assertions.assertThat(result).hasSize(1)
        Assertions.assertThat(result.joinToString("\n"))
                .contains("q1 - 4 ; q_unknown - 1")
    }

    @Test
    fun testThatSpecificAnalysisiInitializesHeader() {
        service.processAction(ActionAnalyze(""))

        val res = Files.readAllLines(specificAnalysisPath)
        Assertions.assertThat(res).hasSize(2)
        Assertions.assertThat(res)
                .contains("timestamp ; q1 ; q2");
        Assertions.assertThat(res.joinToString("\n")).contains("4 ; 0")
    }

    @Test
    fun testThatMultipleCallsWillAppend() {
        service.processAction(ActionAnalyze(""))

        val res1 = Files.readAllLines(analysisPath)
        val res2 = Files.readAllLines(specificAnalysisPath)

        Assertions.assertThat(res1).hasSize(1)
        Assertions.assertThat(res2).hasSize(2)


        service.processAction(ActionAnalyze(""))
        val res1a = Files.readAllLines(analysisPath)
        val res2a = Files.readAllLines(specificAnalysisPath)
        Assertions.assertThat(res1a).hasSize(2)
        Assertions.assertThat(res2a).hasSize(3)
    }


    @AfterEach
    fun destroy() {
        Files.delete(analysisPath)
        Files.delete(specificAnalysisPath)
    }
}