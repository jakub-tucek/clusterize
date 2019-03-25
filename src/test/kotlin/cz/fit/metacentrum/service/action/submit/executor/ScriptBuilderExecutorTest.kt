package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.KotlinMockito
import cz.fit.metacentrum.TestData
import cz.fit.metacentrum.config.FileNames
import cz.fit.metacentrum.domain.template.TemplateData
import cz.fit.metacentrum.service.TemplateService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension

/**
 * @author Jakub Tucek
 */
@ExtendWith(MockitoExtension::class)
internal class ScriptBuilderExecutorTest {

    @InjectMocks
    private lateinit var ex: ScriptBuilderExecutor
    @Mock
    private lateinit var templateService: TemplateService
    @Suppress("unused")
    @Spy
    private var templateDataBuilder = TemplateDataBuilder()

    @Test
    fun testMatlabScriptGeneratedFile() {
        ex.execute(TestData.metadata)

        Mockito.verify(templateService)
                .write(
                        KotlinMockito.eq("templates/matlab.mustache"),
                        KotlinMockito.eq(TestData.metadata.paths.storagePath!!.resolve("0/${FileNames.innerScript}")),
                        KotlinMockito.isA(TemplateData::class.java)
                )
    }
}