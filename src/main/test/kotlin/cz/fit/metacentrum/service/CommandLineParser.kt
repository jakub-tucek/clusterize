package cz.fit.metacentrum.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import cz.fit.metacentrum.service.CommandLineParser


class CommandLineParserTest {

    @Test
    fun testThatInputIsParsedCorrectly() {
        val res = CommandLineParser().parseArguments(arrayOf("-i", "file"))
        Assertions.assertEquals("file", res.inputFile)
    }
}