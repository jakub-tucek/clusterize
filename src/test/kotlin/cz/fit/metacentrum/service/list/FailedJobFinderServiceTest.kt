package cz.fit.metacentrum.service.list

import com.google.common.jimfs.Jimfs
import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption


/**
 * @author Jakub Tucek
 */
internal class FailedJobFinderServiceTest {

    private val failedJobFinderService: FailedJobFinderService = FailedJobFinderService()

    private lateinit var fileSystem: FileSystem
    private lateinit var rootPath: Path

    private lateinit var job1Running: ExecutionMetadataJob
    private lateinit var job2Done: ExecutionMetadataJob
    private lateinit var job3Failed: ExecutionMetadataJob
    private lateinit var job4Deleted: ExecutionMetadataJob


    @BeforeEach
    fun setUp() {
        fileSystem = Jimfs.newFileSystem()
        rootPath = fileSystem.getPath("/")

        // init jobs
        job1Running = initJob("1")
        job2Done = initJob("2")
        job3Failed = initJob("3")
        job4Deleted = initJob("4")

        // init job2
        var status = job2Done.jobPath.resolve(FileNames.statusLog)
        Files.write(status, "0\n".toByteArray(), StandardOpenOption.CREATE_NEW)

        //init job3
        status = job3Failed.jobPath.resolve(FileNames.statusLog)
        Files.write(status, "1\n".toByteArray(), StandardOpenOption.CREATE_NEW)
        Files.write(job3Failed.jobPath.resolve(FileNames.stdJobLog), "STD SCRIPT ERROR\n".toByteArray(), StandardOpenOption.CREATE_NEW)
        Files.write(job3Failed.jobPath.resolve(FileNames.stdErrLog), "STD ERROR\n".toByteArray(), StandardOpenOption.CREATE_NEW)
    }

    private fun initJob(jobName: String): ExecutionMetadataJob {
        val path = rootPath.resolve(jobName)
        Files.createDirectories(path)
        return ExecutionMetadataJob(path, pid = "PID$jobName", jobId = jobName.toInt())
    }

    @Test
    fun testMissingStatusLog() {
        val failedJobs = failedJobFinderService.findFailedJobs(listOf(job4Deleted), emptyList())
        val failedJob = failedJobs.first()
        Assertions.assertThat(failedJob.status).isEqualTo(null)
        Assertions.assertThat(failedJob.job).isEqualTo(job4Deleted)
        Assertions.assertThat(failedJob.output).isBlank()
    }

    @Test
    fun testRunningAndSuccessfulJob() {
        val failedJobs = failedJobFinderService.findFailedJobs(listOf(job1Running, job2Done), listOf(job1Running.pid!!))
        Assertions.assertThat(failedJobs).isEmpty()
    }

    @Test
    fun testFailedJob() {
        val failedJobs = failedJobFinderService.findFailedJobs(listOf(
                job1Running,
                job2Done,
                job3Failed
        ), listOf(job1Running.pid!!))
        Assertions.assertThat(failedJobs)
                .hasSize(1)
        val failedJob = failedJobs.first()
        Assertions.assertThat(failedJob.status).isEqualTo(1)
        Assertions.assertThat(failedJob.job).isEqualTo(job3Failed)
        Assertions.assertThat(failedJob.output).contains(
                "STD SCRIPT ERROR",
                "STD ERROR",
                "1"
        )

    }
}