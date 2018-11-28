package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.service.TestData
import cz.fit.metacentrum.service.executor.submit.MatlabScriptsExecutor
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

        Assertions.assertThat(TestData.metadata.storagePath!!.resolve("0/inner_script.sh"))
                .exists()
                .satisfies { Files.lines(it).anyMatch { it.contains("module add matlab") } }
    }
}