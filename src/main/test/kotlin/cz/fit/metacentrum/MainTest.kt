package cz.fit.metacentrum

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test


class MainTest {

    @Test
    fun theMethodShouldThrowIllegalStateException() {
        assertThrows(IllegalStateException::class.java) {
            iThrowAnException()
        }

        println("It definitely threw that IllegalStateException.")
    }

    private fun iThrowAnException() {
        throw IllegalStateException()
    }

}