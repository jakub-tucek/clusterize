package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.service.TestData
import cz.fit.metacentrum.service.executor.submit.InitOutputPathExecutor
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * @author Jakub Tucek
 */
internal class InitOutputPathExecutorIntegTest {

    val executor = InitOutputPathExecutor()

    var outputDir: Path? = null
    var metadataOutputDir: Path? = null

    @Test()
    fun testThatDirectoryIsActuallyCreated() {
        val property = "java.io.tmpdir"
        // Get the temporary directory and print it.
        val tempDir = System.getProperty(property)

        val basePath = Paths.get(tempDir)

        val metadata = executor.execute(
                ExecutionMetadata(
                        configFile = TestData.config.copy(environment = TestData.config.environment
                                .copy(
                                        storagePath = basePath.resolve("storage").toString(),
                                        metadataStoragePath = basePath.resolve("metadata").toString()
                                )
                        )

                )
        )
        outputDir = metadata.storagePath
        metadataOutputDir = metadata.metadataStoragePath


        checkOutDirIsCreated(outputDir!!)
        checkOutDirIsCreated(metadataOutputDir!!)
    }

    private fun checkOutDirIsCreated(dir: Path) {
        Assertions.assertThat(dir)
                .exists()
                .extracting { t -> t.fileName.toString() }
                .asString()
                .startsWith(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
    }

    @AfterEach
    fun cleanUp() {
        if (outputDir != null) {
            Files.deleteIfExists(outputDir)
        }
        if (metadataOutputDir != null) {
            Files.deleteIfExists(metadataOutputDir)
        }
    }

}