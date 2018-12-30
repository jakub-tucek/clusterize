package cz.fit.metacentrum.service.action.status

import cz.fit.metacentrum.TestData
import cz.fit.metacentrum.config.userDateFormat
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataState
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


    private lateinit var doneMetadata: ExecutionMetadata
    private lateinit var failedMetadata: ExecutionMetadata
    private lateinit var runningMetadata: ExecutionMetadata

    private val systemOut = System.out
    private var testOut: ByteArrayOutputStream? = null

    @BeforeEach
    fun init() {
        val (job1, job2, job3) = TestData.executedMetadata.jobs!!
        val failedJob = job1.copy(
                jobInfo = job1.jobInfo.copy(
                        status = 1,
                        state = ExecutionMetadataState.FAILED,
                        output = "err"
                )
        )
        val runningJob = job2.copy(
                jobInfo = job2.jobInfo.copy(
                        state = ExecutionMetadataState.RUNNING,
                        runningTime = "00:00:00"
                )
        )
        val doneJob = job3.copy(
                jobInfo = job3.jobInfo.copy(
                        state = ExecutionMetadataState.DONE
                )
        )
        doneMetadata = TestData.executedMetadata.copy(
                jobs = listOf(doneJob),
                currentState = ExecutionMetadataState.DONE
        )
        failedMetadata = TestData.executedMetadata.copy(
                jobs = listOf(doneJob, failedJob),
                currentState = ExecutionMetadataState.FAILED
        )
        runningMetadata = TestData.executedMetadata.copy(
                jobs = listOf(runningJob, failedJob, doneJob),
                currentState = ExecutionMetadataState.RUNNING
        )


        testOut = ByteArrayOutputStream()
        System.setOut(PrintStream(testOut))
    }

    @AfterEach
    fun destroy() {
        System.setOut(systemOut)
    }

    @Test
    fun testPrintingMetadataInfo() {
        MetadataInfoPrinter().printMetadataListInfo(listOf(doneMetadata, failedMetadata, runningMetadata))


        System.setOut(systemOut)

        val out = testOut.toString()
        val time = TestData.executedMetadata.creationTime.format(DateTimeFormatter.ofPattern(userDateFormat))
        Assertions.assertThat(out).contains(
                "* 0 - task X - $time - DONE",
                "* 1 - task X - $time - 1/2 FAILED",
                "* 2 - task X - $time - RUNNING",
                "1/3 FAILED",
                "0/3 QUEUED",
                "1/3 RUNNING",
                "2.pid: 00:00:00"
        )
    }

}