package cz.fit.metacentrum.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


internal class CommandLineParserTest {

    @Test
    fun testThatInputIsParsedCorrectly() {
        val res = CommandLineParser().parseArguments(arrayOf("-i", "file"))
        Assertions.assertEquals("file", res.inputFile)
    }

    @Test
    fun testThatInputWithoutArgumentFails() {
        Assertions.assertThrows(
                IllegalArgumentException::class.java
        ) {
            CommandLineParser().parseArguments(arrayOf("-i"))
        }
    }
}
