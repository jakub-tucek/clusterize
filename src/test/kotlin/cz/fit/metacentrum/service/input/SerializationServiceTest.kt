package cz.fit.metacentrum.service.input

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
        Assertions.assertEquals(null, res.environment.taskName)
        Assertions.assertEquals("VICINITY_TYPE", res.iterations[0].name)
    }

    @Test
    fun parseFail() {
        val throwsRes =Assertions.assertThrows(IllegalArgumentException::class.java) {
            SerializationService().parseConfig("$folderPath/configFileTestInvalid.yml")
        }

        Assertions.assertTrue(throwsRes.message!!.isNotBlank())
    }
}