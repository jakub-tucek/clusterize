package cz.fit.metacentrum.service.input

import cz.fit.metacentrum.domain.config.MatlabTaskType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test



/**
 * @author Jakub Tucek
 */
internal class SerializationServiceTest {

    private val folderPath = "src/test/resources/configFileParser"

    @Test
    fun parse() {
        val res = SerializationService().parseConfig("$folderPath/configFileTest.yml")

        Assertions.assertEquals(false, res.iterations.isEmpty())
        Assertions.assertEquals("matlab-dip", res.general.taskName)
        Assertions.assertEquals("VICINITY_TYPE", res.iterations[0].name)
        val task = res.taskType as MatlabTaskType
        Assertions.assertEquals(
                "main_batch01(\$MIN_TRANSL, \$MAX_TRANSL, \$VICINITY_TYPE, \$SUB_IMG_IDX, 'useGPU', 'yes', 'layers', \$ADDED_LAYERS)",
                task.functionCall
        )
    }

    @Test
    fun parseFail() {
        val throwsRes =Assertions.assertThrows(IllegalArgumentException::class.java) {
            SerializationService().parseConfig("$folderPath/configFileTestInvalid.yml")
        }

        Assertions.assertTrue(throwsRes.message!!.isNotBlank())
    }
}