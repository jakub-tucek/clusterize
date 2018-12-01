package cz.fit.metacentrum.service.list

import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.meta.*
import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.reflect.KClass

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

    @BeforeEach
    fun init() {
        metadata = TestData.executedMetadata
    }


    @Test
    fun testAllOk() {
        // keep only first job
        val shortenedMetadaJobs = metadata.copy(jobs = metadata.jobs!!.subList(0, 1))

        Mockito.`when`(failedJobFinderService.findFailedJobs(shortenedMetadaJobs.jobs!!)).thenReturn(emptyList())
        Mockito.`when`(queueRecordsService.retrieveQueueForUser(metadata.submittingUsername!!))
                .thenReturn(emptyList())

        val result = checkQueueExecutor.execute(shortenedMetadaJobs)

        retrieveState<ExecutionMetadataStateRunning>(
                result.state,
                ExecutionMetadataStateOk::class
        )
    }


    @Test
    fun testRunningWithFailedAndOkJob() {
        val (_, queuedJob, runningJob) = metadata.jobs!!

        // setup failed job
        val failWrapper = Mockito.mock(ExecutionMetadataJobFailedWrapper::class.java)
        Mockito.`when`(failedJobFinderService.findFailedJobs(metadata.jobs!!)).thenReturn(listOf(failWrapper))

        // setup queue records
        val queuedRecord = Mockito.mock(QueueRecord::class.java)
        Mockito.`when`(queuedRecord.pid).thenReturn(queuedJob.pid!!)
        Mockito.`when`(queuedRecord.state).thenReturn(QueueRecord.State.QUEUED)
        Mockito.`when`(queuedRecord.elapsedTime).thenReturn("")

        val runningRecord = Mockito.mock(QueueRecord::class.java)
        Mockito.`when`(runningRecord.pid).thenReturn(runningJob.pid!!)
        Mockito.`when`(runningRecord.state).thenReturn(QueueRecord.State.RUNNING)
        Mockito.`when`(runningRecord.elapsedTime).thenReturn("")

        Mockito.`when`(queueRecordsService.retrieveQueueForUser(metadata.submittingUsername!!)).thenReturn(listOf(
                queuedRecord, runningRecord
        ))
        val result = checkQueueExecutor.execute(metadata)

        val res = retrieveState<ExecutionMetadataStateRunning>(
                result.state,
                ExecutionMetadataStateRunning::class
        )
        Assertions.assertThat(res.queuedJobs).extracting<ExecutionMetadataJob> { it.job }.contains(queuedJob)
        Assertions.assertThat(res.runningJobs).extracting<ExecutionMetadataJob> { it.job }.contains(runningJob)
        Assertions.assertThat(res.failedJobs).contains(failWrapper)

    }


    @Nested
    inner class AllJobsWithSameStatus() {
        @Test
        fun testAllQueued() {
            testAllJobsWithSameStatusInQueue(
                    metadata.jobs!!.map { createQueueJob(it) },
                    { state: ExecutionMetadataStateRunning -> state.queuedJobs },
                    { state: ExecutionMetadataStateRunning -> state.runningJobs }
            )
        }

        @Test
        fun testAllRunning() {
            val allRunningMocks = metadata.jobs!!
                    .map { createQueueJob(it) }
                    .map {
                        Mockito.`when`(it.state).thenReturn(QueueRecord.State.RUNNING)
                        it
                    }
            testAllJobsWithSameStatusInQueue(
                    allRunningMocks,
                    { state: ExecutionMetadataStateRunning -> state.runningJobs },
                    { state: ExecutionMetadataStateRunning -> state.queuedJobs }
            )
        }

        private fun testAllJobsWithSameStatusInQueue(jobs: List<QueueRecord>,
                                                     getActiveJobs: (ExecutionMetadataStateRunning) -> List<ExecutionMetadataJobRunningWrapper>,
                                                     getEmptyJobs: (ExecutionMetadataStateRunning) -> List<ExecutionMetadataJobRunningWrapper>
        ) {

            // return created jobs
            Mockito.`when`(queueRecordsService.retrieveQueueForUser(metadata.submittingUsername!!))
                    .thenReturn(jobs)
            Mockito.`when`(failedJobFinderService.findFailedJobs(metadata.jobs!!)).thenReturn(emptyList())

            // run
            val result = checkQueueExecutor.execute(metadata)

            val state = retrieveState<ExecutionMetadataStateRunning>(
                    result.state,
                    ExecutionMetadataStateRunning::class
            )
            Assertions.assertThat(state.failedJobs).isEmpty()
            Assertions.assertThat(getEmptyJobs(state)).isEmpty()

            Assertions.assertThat(getActiveJobs(state))
                    .extracting<ExecutionMetadataJob> { it.job }
                    .isEqualTo(metadata.jobs!!)
            Assertions.assertThat(getActiveJobs(state))
                    .extracting<String> { it.runTime }
                    .contains("00:00:00")

        }
    }

    fun createQueueJob(job: ExecutionMetadataJob): QueueRecord {
        val mock = Mockito.mock(QueueRecord::class.java)
        Mockito.`when`(mock.pid).thenReturn(job.pid!!)
        Mockito.`when`(mock.elapsedTime).thenReturn("00:00:00")
        Mockito.`when`(mock.state).thenReturn(QueueRecord.State.QUEUED)
        return mock
    }

    // retrieve concrete instance of ExecutionMetadataState
    @Suppress("UNCHECKED_CAST")
    fun <T> retrieveState(state: ExecutionMetadataState?, kClass: KClass<out Any>): T {
        Assertions.assertThat(state!!).isInstanceOf(kClass.java)
        return state as T
    }

}