package cz.fit.metacentrum.service.submit.executor

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Files

/**
 * @author Jakub Tucek
 */
internal class MatlabScriptsExecutorTest {


    val ex = MatlabScriptsExecutor()

    @Test
    fun testMatlabScriptGeneratedFile() {
        ex.execute(TestData.metadata)

        Assertions.assertThat(TestData.metadata.storagePath!!.resolve("0/${FileNames.innerScript}"))
                .exists()
                .satisfies { Files.lines(it).anyMatch { it.contains("module add matlab") } }
    }
}