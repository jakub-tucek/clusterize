package cz.fit.metacentrum.service.submit.executor

import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import java.nio.file.Files

/**
 * @author Jakub Tucek
 */
@ExtendWith(MockitoExtension::class)
internal class MatlabScriptsExecutorTest {

    @InjectMocks
    private lateinit var ex: MatlabScriptsExecutor
    @Spy
    private var templateDataBuilder = MatlabTemplateDataBuilder()

    @Test
    fun testMatlabScriptGeneratedFile() {
        ex.execute(TestData.metadata)

        Assertions.assertThat(TestData.metadata.storagePath!!.resolve("0/${FileNames.innerScript}"))
                .exists()
                .satisfies { Files.lines(it).anyMatch { it.contains("module add matlab") } }
    }
}