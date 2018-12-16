package cz.fit.metacentrum.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.nio.file.Files

/**
 * @author Jakub Tucek
 */
internal class TemplateServiceTest {

    val templateService: TemplateService = TemplateService()

    val outFile = Files.createTempFile("TemplateServiceTest", "template")

    @Test
    fun testTemplateCreation() {
        templateService.write(
                "test-templates/dummy-template.mustache",
                outFile,
                DummyTemplateData("bodyText")
        )

        Assertions.assertThat(Files.readAllLines(outFile))
                .contains("bodyText")
    }

    private data class DummyTemplateData(val property: String)

    @AfterEach
    fun cleanUp() {
        Files.deleteIfExists(outFile)
    }

}