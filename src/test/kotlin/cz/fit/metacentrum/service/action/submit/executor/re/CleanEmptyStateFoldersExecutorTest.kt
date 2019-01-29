package cz.fit.metacentrum.service.action.submit.executor.re

import cz.fit.metacentrum.FileSystemExtension
import cz.fit.metacentrum.TestData
import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.service.action.resubmit.CleanEmptyStateFoldersExecutor
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import java.nio.file.Files

/**
 * @author Jakub Tucek
 */
@ExtendWith(FileSystemExtension::class)
internal class CleanEmptyStateFoldersExecutorTest {

    private val executor: CleanEmptyStateFoldersExecutor = CleanEmptyStateFoldersExecutor()


    @Test
    fun testThatAllExceptScriptIsDeleted() {
        val metadata = TestData.toRerunMetadata
        initJobsFolder(metadata.jobs!!)

        val newMetadata = executor.execute(metadata)

        val firstJobPath = newMetadata.jobs!!.first().jobPath
        Assertions.assertThat(firstJobPath.toString())
                .isEqualTo("/storage/job1_RERUN_0")
        Files.exists(firstJobPath.resolve(FileNames.innerScript))
    }

    @Test
    fun testRerunOfRerunHasNiceFolderName() {
        val historyMock = Mockito.mock(ExecutionMetadataJob::class.java)

        val rerunJobs = TestData.toRerunMetadata.jobs!!.map {
            val newJobPath = TestData.fileSystem
                    .getPath("/storage")
                    .resolve(it.jobPath.fileName.toString() + "_RERUN_1")
            it.copy(jobPath = newJobPath, jobParent = historyMock)
        }
        val rerunMetadata = TestData.toRerunMetadata.copy(jobs = rerunJobs)
        initJobsFolder(rerunMetadata.jobs!!)

        val newMetadata = executor.execute(rerunMetadata)

        val firstJobPath = newMetadata.jobs!!.first().jobPath
        Assertions.assertThat(firstJobPath.toString())
                .isEqualTo("/storage/job1_RERUN_0")
    }

    private fun initJobsFolder(jobs: List<ExecutionMetadataJob>) {
        jobs.forEach {
            Files.createDirectories(it.jobPath)
            Files.createFile(it.jobPath.resolve(FileNames.innerScript))
        }

    }

}