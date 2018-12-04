package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

/**
 * @author Jakub Tucek
 */
internal class TaskNameConfiguratorTest {

    private val systemIn = System.`in`
    private var testIn: ByteArrayInputStream? = null

    @AfterEach
    fun destroy() {
        System.setIn(systemIn)
    }

    @Test
    fun testJobname() {
        testIn = ByteArrayInputStream("name 2".toByteArray())
        System.setIn(testIn)

        val res = TaskNameConfigurator().configureInteractively(TestData.config)
        Assertions.assertThat(res.general.taskName).isEqualTo("name 2")
    }
}