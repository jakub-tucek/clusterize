package cz.fit.metacentrum.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

/**
 * @author Jakub Tucek
 */
internal class ConsoleReaderTest {

    private val systemIn = System.`in`
    private var testIn: ByteArrayInputStream? = null
    private val consoleReader = ConsoleReader()

    @AfterEach
    fun destroy() {
        System.setIn(systemIn)
    }

    private fun writeToConsole(msg: String) {
        testIn = ByteArrayInputStream(msg.toByteArray())
        System.setIn(testIn)
    }


    @Test
    fun testAskForConfirmYes() {
        writeToConsole("YES")
        val res = consoleReader.askForConfirmation("msg", false)

        Assertions.assertThat(res).isTrue()
    }


    @Test
    fun testAskForConfirmNo() {
        writeToConsole("nO")
        val res = consoleReader.askForConfirmation("msg", true)

        Assertions.assertThat(res).isFalse()
    }

    @Test
    fun testReadingInput() {
        writeToConsole("\n\n\n123")
        val res = consoleReader.askForValue("msg") { v ->
            if (v.isBlank()) null else v.toInt()
        }
        Assertions.assertThat(res).isEqualTo(123)
    }

    @Test
    fun testReadingValidEmail() {
        writeToConsole("invalid\nmail@asd\nmail@correct.cz")
        val res = consoleReader.askForEmail("msg")

        Assertions.assertThat(res).isEqualTo("mail@correct.cz")
    }
}