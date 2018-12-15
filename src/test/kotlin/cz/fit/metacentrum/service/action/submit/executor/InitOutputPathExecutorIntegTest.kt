package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.service.TestData
import cz.fit.metacentrum.util.FileUtils
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * @author Jakub Tucek
 */
internal class InitOutputPathExecutorIntegTest {

    val executor = InitOutputPathExecutor()

    var storagePath: Path? = null
    var metadataOutputDir: Path? = null


    @Test()
    fun testThatDirectoryIsActuallyCreated() {
        val property = "java.io.tmpdir"
        // Get the temporary directory and print it.
        val tempDir = System.getProperty(property)
        val basePath = Paths.get(tempDir)

        storagePath = basePath.resolve("storage")
        metadataOutputDir = basePath.resolve("metadata")


        val metadata = executor.execute(
                ExecutionMetadata(
                        configFile = TestData.config.copy(general = TestData.config.general
                                .copy(
                                        storagePath = storagePath.toString(),
                                        metadataStoragePath = metadataOutputDir.toString()
                                )
                        )

                )
        )
        val newStoragePath = metadata.paths.storagePath
        val newMetadataPath = metadata.paths.metadataStoragePath


        checkOutDirIsCreated(newStoragePath!!)
        checkOutDirIsCreated(newMetadataPath!!)
    }

    private fun checkOutDirIsCreated(dir: Path) {
        Assertions.assertThat(dir)
                .exists()
                .extracting { t -> t.fileName.toString() }
                .asString()
                .startsWith("task-1__" + LocalDate.now().format(DateTimeFormatter.ISO_DATE))
    }

    @AfterEach
    fun cleanUp() {
        if (storagePath != null) {
            FileUtils.deleteFolder(storagePath!!)
        }
        if (metadataOutputDir != null) {
            FileUtils.deleteFolder(metadataOutputDir!!)
        }
    }

}