package cz.fit.metacentrum.service.submit.executor

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataPath
import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.nio.file.Files


/**
 * @author Jakub Tucek
 */
internal class InitOutputPathExecutorTest {

    val executor = InitOutputPathExecutor()
    val fs = Jimfs.newFileSystem(Configuration.unix())


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