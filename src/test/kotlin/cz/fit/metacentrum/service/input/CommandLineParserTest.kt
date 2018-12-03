package cz.fit.metacentrum.service.input

import cz.fit.metacentrum.domain.ActionSubmitPath
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test


internal class CommandLineParserTest {

    @Test
    fun testThatInputIsParsedCorrectly() {
        val res = CommandLineParser().parseArguments(arrayOf("submit", "file"))
        Assertions.assertThat(res)
                .isInstanceOf(ActionSubmitPath::class.java)
                .extracting { t -> (t as ActionSubmitPath).configFilePath }
                .isSameAs("file")
    }

    @Test
    fun testThatInputWithoutArgumentFails() {
        assertThrows(
                IllegalArgumentException::class.java
        ) {
            CommandLineParser().parseArguments(arrayOf("-c"))
        }
    }
}
