package cz.fit.metacentrum.service.action.submit.executor.re

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.meta.ExecutionMetadataStateFailed
import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Files

/**
 * @author Jakub Tucek
 */
internal class CleanFailedJobDirectoryExecutorTest {

    val executor: CleanFailedJobDirectoryExecutor = CleanFailedJobDirectoryExecutor()


    @Test
    fun testThatAllExceptScriptIsDeleted() {
        val data = TestData.failedMetadata

        // init job directories
        data.jobs!!.forEach {
            Files.createDirectories(it.jobPath)
            Files.write(it.jobPath.resolve(FileNames.innerScript), "inner script content ${it.jobId}".toByteArray())
            Files.write(it.jobPath.resolve("someFile"), "some file content".toByteArray())
        }

        val result = executor.execute(data)

        Assertions.assertThat(result.jobs)
                .allMatch { t -> Files.exists(t.jobPath) }
                .allMatch { t -> Files.exists(t.jobPath.resolve(FileNames.innerScript)) }
                .allMatch { t -> Files.readAllBytes(t.jobPath.resolve(FileNames.innerScript))!!.contentEquals("inner script content ${t.jobId}".toByteArray()) }

        val state = result.state as ExecutionMetadataStateFailed
        Assertions.assertThat(
                result.jobs!!.filter { job ->
                    state.failedJobs.find { it.job.jobId == job.jobId } == null
                }
        )
                .hasSize(2)
                .allMatch { t -> Files.exists(t.jobPath.resolve("someFile")) }
                .allMatch { t -> Files.readAllBytes(t.jobPath.resolve("someFile"))!!.contentEquals("some file content".toByteArray()) }


    }

}