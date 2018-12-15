package cz.fit.metacentrum.service.action.list

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

        Mockito.`when`(failedJobFinderService.findFailedJobs(shortenedMetadaJobs.jobs!!, emptyList())).thenReturn(emptyList())
        Mockito.`when`(queueRecordsService.retrieveQueueForUser(metadata.submittingUsername!!))
                .thenReturn(emptyList())

        val result = checkQueueExecutor.execute(shortenedMetadaJobs)

        retrieveState<ExecutionMetadataStateRunning>(
                result.state,
                ExecutionMetadataStateDone::class
        )
    }


    @Test
    fun testRunningWithFailedAndOkJob() {
        val (_, queuedJob, runningJob) = metadata.jobs!!

        // setup failed job
        val failWrapper = Mockito.mock(ExecutionMetadataJobFailedWrapper::class.java)
        Mockito.`when`(failedJobFinderService.findFailedJobs(metadata.jobs!!, listOf(queuedJob.pid!!, runningJob.pid!!))).thenReturn(listOf(failWrapper))

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
            testAllJobsWithSameStatusInQueue(metadata.jobs!!.map { createQueueJob(it) })
            { state: ExecutionMetadataStateRunning ->
                Assertions.assertThat(state.runningJobs).isEmpty()

                Assertions.assertThat(state.queuedJobs)
                        .extracting<ExecutionMetadataJob> { it.job }
                        .isEqualTo(metadata.jobs!!)
                Assertions.assertThat(state.queuedJobs)
                        .extracting<String> { it.runTime }
                        .contains("00:00:00")
            }
        }

        @Test
        fun testAllRunning() {
            // setup all jobs and make them all have running state
            val allRunningMocks = metadata.jobs!!
                    .map { createQueueJob(it) }
                    .map {
                        Mockito.`when`(it.state).thenReturn(QueueRecord.State.RUNNING)
                        it
                    }
            testAllJobsWithSameStatusInQueue(allRunningMocks)
            { state: ExecutionMetadataStateRunning ->
                Assertions.assertThat(state.queuedJobs).isEmpty()

                Assertions.assertThat(state.runningJobs)
                        .extracting<ExecutionMetadataJob> { it.job }
                        .isEqualTo(metadata.jobs!!)
            }
        }

        private fun testAllJobsWithSameStatusInQueue(jobs: List<QueueRecord>,
                                                     checkState: (ExecutionMetadataStateRunning) -> Unit) {

            // return created jobs
            Mockito.`when`(queueRecordsService.retrieveQueueForUser(metadata.submittingUsername!!))
                    .thenReturn(jobs)
            val jobPids = jobs.map { it.pid }
            Mockito.`when`(failedJobFinderService.findFailedJobs(metadata.jobs!!, jobPids)).thenReturn(emptyList())

            // run
            val result = checkQueueExecutor.execute(metadata)

            val state = retrieveState<ExecutionMetadataStateRunning>(
                    result.state,
                    ExecutionMetadataStateRunning::class
            )
            Assertions.assertThat(state.failedJobs).isEmpty()
            // call specific assert
            checkState(state)
        }


        private fun createQueueJob(job: ExecutionMetadataJob): QueueRecord {
            val mock = Mockito.mock(QueueRecord::class.java)
            Mockito.`when`(mock.pid).thenReturn(job.pid!!)
            Mockito.`when`(mock.elapsedTime).thenReturn("00:00:00")
            Mockito.`when`(mock.state).thenReturn(QueueRecord.State.QUEUED)
            return mock
        }
    }


    // retrieve concrete instance of ExecutionMetadataState
    @Suppress("UNCHECKED_CAST")
    fun <T> retrieveState(state: ExecutionMetadataState?, kClass: KClass<out Any>): T {
        Assertions.assertThat(state!!).isInstanceOf(kClass.java)
        return state as T
    }

}