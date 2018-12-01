package cz.fit.metacentrum.service.list

import cz.fit.metacentrum.config.userDateFormat
import cz.fit.metacentrum.domain.meta.*
import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.time.format.DateTimeFormatter


/**
 * @author Jakub Tucek
 */
internal class MetadataInfoPrinterTest {


    private lateinit var okJob: ExecutionMetadata
    private lateinit var failJob: ExecutionMetadata
    private lateinit var runningJob: ExecutionMetadata

    private val systemOut = System.out
    private var testOut: ByteArrayOutputStream? = null

    @BeforeEach
    fun init() {
        val firstJobFailedWrappers = listOf(
                ExecutionMetadataJobFailedWrapper(TestData.executedMetadata.jobs!!.first(), 1, "err"))
        val runningJobWrappers = listOf(
                ExecutionMetadataJobRunningWrapper(
                        TestData.executedMetadata.jobs!!.first(),
                        "00:00:00"
                )
        )

        okJob = TestData.executedMetadata.copy(state = ExecutionMetadataStateOk)
        failJob = TestData.executedMetadata.copy(state = ExecutionMetadataStateFailed(firstJobFailedWrappers))
        runningJob = TestData.executedMetadata.copy(state = ExecutionMetadataStateRunning(
                failedJobs = firstJobFailedWrappers,
                runningJobs = runningJobWrappers,
                queuedJobs = runningJobWrappers
        ))

        testOut = ByteArrayOutputStream()
        System.setOut(PrintStream(testOut))
    }

    @AfterEach
    fun destroy() {
        System.setOut(systemOut);
    }

    @Test
    fun testPrintingMetadataInfo() {
        MetadataInfoPrinter().printMetadataListInfo(listOf(okJob, failJob, runningJob))


        System.setOut(systemOut);

        val out = testOut.toString()
        val time = TestData.executedMetadata.timestamp.format(DateTimeFormatter.ofPattern(userDateFormat))
        Assertions.assertThat(out).contains(
                "* 0 - $time - OK",
                "* 1 - $time - 1/3 FAILED",
                "* 2 - $time - RUNNING",
                "* 2 - $time - RUNNING",
                "1/3 FAILED",
                "1/3 QUEUED",
                "1/3 RUNNING",
                "1.pid: 00:00:00"
        )
    }

}