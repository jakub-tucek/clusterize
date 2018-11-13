package cz.fit.metacentrum.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

/**
 * @author Jakub Tucek
 */
internal class ConfigFileParserTest {

    @Test
    fun parse() {
        val res = ConfigFileParser().parse("src/main/test/resources/configFileParser/configFileTest.yml")

        Assertions.assertEquals(false, res.variables.isEmpty())
        Assertions.assertEquals("SUPER ASDASD", res.variables.get("RUN_APP"))
    }

    @Test
    fun parseFail() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            ConfigFileParser().parse("src/main/test/resources/configFileParser/configFileTestEmpty.yml")
        }
    }

    @Test
    fun parseEmptyVariables() {
        val res = ConfigFileParser().parse("src/main/test/resources/configFileParser/configFileTestEmptyVars.yml")

        Assertions.assertEquals(true, res.variables.isEmpty())
    }
}