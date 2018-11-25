package cz.fit.metacentrum.service.executor

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.config.ConfigEnvironment
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
        val foo = fs.getPath("path/is/mocked")

        Assertions.assertThat(foo).doesNotExist()

        executor.execute(ExecutionMetadata(
                foo,
                TestData.config.copy(environment = ConfigEnvironment(foo.toString(), emptyMap()))
        ))

        Assertions.assertThat(foo).exists()
    }


    @Test
    fun testThatDirectoryIsNotCreatedIfMockedExists() {
        val foo = fs.getPath("path/is/mocked")

        Files.createDirectories(foo)

        assertThrows(IllegalStateException::class.java) {
            executor.execute(ExecutionMetadata(
                    foo,
                    TestData.config.copy(environment = ConfigEnvironment(foo.toString(), emptyMap()))
            ))
        }
    }
}