package cz.fit.metacentrum.service.action.submit.executor

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import cz.fit.metacentrum.TestData
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataPath
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.nio.file.Files


/**
 * @author Jakub Tucek
 */
internal class InitOutputPathExecutorTest {

    private val executor = InitOutputPathExecutor()
    private val fs = Jimfs.newFileSystem(Configuration.unix())


    @Test
    fun testThatDirectoryIsCreatedWithMockedPath() {
        val path = fs.getPath("path/is/mocked")

        Assertions.assertThat(path).doesNotExist()

        executor.execute(ExecutionMetadata(
                paths = ExecutionMetadataPath(storagePath = path),
                configFile = TestData.config.copy(general = TestData.config.general.copy(storagePath = path.toString()))
        ))

        Assertions.assertThat(path).exists()
    }


    @Test
    fun testThatDirectoryIsNotCreatedIfMockedExists() {
        val path = fs.getPath("path/is/mocked")

        Files.createDirectories(path)

        assertThrows(IllegalStateException::class.java) {
            executor.execute(ExecutionMetadata(
                    paths = ExecutionMetadataPath(storagePath = path),
                    configFile = TestData.config.copy(general = TestData.config.general.copy(storagePath = path.toString()))
            ))
        }
    }
}