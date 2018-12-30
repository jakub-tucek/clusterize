package cz.fit.metacentrum.service.config

import cz.fit.metacentrum.KotlinMockito
import cz.fit.metacentrum.TestData
import cz.fit.metacentrum.domain.config.MatlabTaskType
import cz.fit.metacentrum.service.ConsoleReader
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

/**
 * @author Jakub Tucek
 */
@ExtendWith(MockitoExtension::class)
internal class TaskNameConfiguratorTest {

    @InjectMocks
    private lateinit var taskNameConfigurator: TaskNameConfigurator
    @Mock
    private lateinit var consoleReader: ConsoleReader


    @Test
    fun testJobname() {
        mockConsoleReaderAsForValue("name 2")
        val res = taskNameConfigurator.configureInteractively(TestData.config)
        Assertions.assertThat(res.general.taskName).isEqualTo("name 2")
    }

    @Test
    fun testJobnameIfNotGiven() {
        mockConsoleReaderAsForValue("")
        val res = taskNameConfigurator.configureInteractively(TestData.config)
        Assertions.assertThat(res.general.taskName).isEqualTo(MatlabTaskType::class.simpleName)
    }

    private fun mockConsoleReaderAsForValue(value: String?) {
        Mockito.`when`(consoleReader.askForValue<String>(Mockito.anyString(), KotlinMockito.any()))
                .then { invocation ->
                    @Suppress("UNCHECKED_CAST")
                    val formatter = invocation.getArgument<Any>(1) as (String?) -> String?


                    formatter(value)
                }
    }
}