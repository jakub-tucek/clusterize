package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.TestData
import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
import cz.fit.metacentrum.service.action.status.ex.CheckQueueExecutor
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.nio.file.Files

/**
 * @author Jakub Tucek
 */
@ExtendWith(MockitoExtension::class)
internal class CheckQueueExecutorTest {

    @InjectMocks
    private lateinit var checkQueueExecutor: CheckQueueExecutor

    @Mock
    private lateinit var queueRecordsService: QueueRecordsService
    @Mock
    private lateinit var queueRecord: QueueRecord

    private lateinit var metadata: ExecutionMetadata

    @BeforeEach
    fun init() {
        metadata = TestData.executedMetadata

        // keep only first job
        val job = metadata.jobs!!.first()

        Files.createDirectories(job.jobPath)
        metadata = metadata.copy(jobs = listOf(job))
    }


    @Test
    fun testKilledBecauseItHasNoRecord() {
        Mockito.`when`(queueRecordsService.retrieveQueueForUser(metadata.submittingUsername!!))
                .thenReturn(emptyList())

        val result = checkQueueExecutor.execute(metadata)

        Assertions.assertThat(result.jobs!!.first().jobInfo.state).isEqualTo(ExecutionMetadataState.FAILED)
    }

    @Test
    fun testRunningBecauseQueueServiceSaysSo() {
        Mockito.`when`(queueRecordsService.retrieveQueueForUser(metadata.submittingUsername!!))
                .thenReturn(listOf(queueRecord))
        Mockito.`when`(queueRecord.pid).thenReturn(metadata.jobs!!.first().jobInfo.pid)
        Mockito.`when`(queueRecord.state).thenReturn(QueueRecord.State.RUNNING)

        val result = checkQueueExecutor.execute(metadata)

        Assertions.assertThat(result.jobs!!.first().jobInfo.state).isEqualTo(ExecutionMetadataState.RUNNING)
    }
}