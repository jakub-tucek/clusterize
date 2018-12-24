package cz.fit.metacentrum.service.action.submit.executor.re

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Files

/**
 * @author Jakub Tucek
 */
internal class CleanEmptyStateFoldersExecutorTest {

    private val executor: CleanEmptyStateFoldersExecutor = CleanEmptyStateFoldersExecutor()


    @Test
    fun testThatAllExceptScriptIsDeleted() {
        val metadata = TestData.toRerunMetadata

        metadata.jobs!!.forEach {
            Files.createDirectories(it.jobPath)
            Files.createFile(it.jobPath.resolve(FileNames.innerScript))
        }

        val newMetadata = executor.execute(metadata)

        val firstJobPath = newMetadata.jobs!!.first().jobPath
        Assertions.assertThat(firstJobPath.toString())
                .isEqualTo("/storage/job1_RERUN_1")
        Files.exists(firstJobPath.resolve(FileNames.innerScript))

    }

}