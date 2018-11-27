package cz.fit.metacentrum.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test



/**
 * @author Jakub Tucek
 */
internal class ConfigFileParserTest {

    private val folderPath = "src/test/resources/configFileParser"

    @Test
    fun parse() {
        val res = ConfigFileParser().parse("$folderPath/configFileTest.yml")

        Assertions.assertEquals(false, res.iterations.isEmpty())
        Assertions.assertEquals("VICINITY_TYPE", res.iterations[0].name)
    }

    @Test
    fun parseFail() {
        val throwsRes =Assertions.assertThrows(IllegalArgumentException::class.java) {
            ConfigFileParser().parse("$folderPath/configFileTestInvalid.yml")
        }

        Assertions.assertTrue(throwsRes.message!!.isNotBlank())
    }
}