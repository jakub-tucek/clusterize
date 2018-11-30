package cz.fit.metacentrum.service.list

import com.google.common.jimfs.Jimfs
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
    private lateinit var job2Ok: ExecutionMetadataJob
    private lateinit var job3Failed: ExecutionMetadataJob


    @BeforeEach
    fun setUp() {
        fileSystem = Jimfs.newFileSystem()
        rootPath = fileSystem.getPath("/")

        // init jobs
        job1Running = initJob("1")
        job2Ok = initJob("2")
        job3Failed = initJob("3")

        // init job2
        var status = job2Ok.runPath.resolve("status.log")
        Files.write(status, "0\n".toByteArray(), StandardOpenOption.CREATE_NEW)

        //init job3
        status = job3Failed.runPath.resolve("status.log")
        Files.write(status, "1\n".toByteArray(), StandardOpenOption.CREATE_NEW)
        Files.write(job3Failed.runPath.resolve("stderr_script.log"), "STD SCRIPT ERROR\n".toByteArray(), StandardOpenOption.CREATE_NEW)
        Files.write(job3Failed.runPath.resolve("stderr.log"), "STD ERROR\n".toByteArray(), StandardOpenOption.CREATE_NEW)
    }

    private fun initJob(jobName: String): ExecutionMetadataJob {
        val path = rootPath.resolve(jobName)
        Files.createDirectories(path)
        return ExecutionMetadataJob(path, pid = "123", runId = 12)
    }

    @Test
    fun testMissingStatusLog() {
        val failedJobs = failedJobFinderService.findFailedJobs(listOf(job1Running))
        Assertions.assertThat(failedJobs).isEmpty()
    }

    @Test
    fun testRunningAndSuccessfulJob() {
        val failedJobs = failedJobFinderService.findFailedJobs(listOf(job1Running, job2Ok))
        Assertions.assertThat(failedJobs).isEmpty()
    }

    @Test
    fun testFailedJob() {
        val failedJobs = failedJobFinderService.findFailedJobs(listOf(
                job1Running,
                job2Ok,
                job3Failed
        ))
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