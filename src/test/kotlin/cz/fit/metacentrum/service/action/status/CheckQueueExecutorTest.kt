package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.KotlinMockito
import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

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
    private lateinit var failedJobFinderService: FailedJobFinderService

    private lateinit var metadata: ExecutionMetadata

    private lateinit var queueRecordMap: Map<String, List<QueueRecord>>

    @BeforeEach
    fun init() {
        metadata = TestData.executedMetadata
        val (done, queuedJob, runningJob) = metadata.jobs!!
        queueRecordMap = mapOf<String, List<QueueRecord>>(
                done.jobInfo.pid!! to listOf(Mockito.mock(QueueRecord::class.java)),
                queuedJob.jobInfo.pid!! to listOf(Mockito.mock(QueueRecord::class.java)),
                runningJob.jobInfo.pid!! to listOf(Mockito.mock(QueueRecord::class.java))
        )
    }


    @Test
    fun testAllOk() {
        // keep only first job
        val doneJob = metadata.jobs!!.subList(0, 1)
        val shortenedMetadaJobs = metadata.copy(jobs = doneJob)

        Mockito.`when`(failedJobFinderService.updateJobState(KotlinMockito.eq(shortenedMetadaJobs.jobs!!), KotlinMockito.any()))
                .thenReturn(emptyList())
        Mockito.`when`(queueRecordsService.retrieveQueueForUser(metadata.submittingUsername!!))
                .thenReturn(emptyList())

        val result = checkQueueExecutor.execute(shortenedMetadaJobs)

        Assertions.assertThat(result.jobs).isEqualTo(emptyList<ExecutionMetadataJob>())
    }
}