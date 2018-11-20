package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


/**
 * @author Jakub Tucek
 */
internal class ConfigFileValidatorTest {

    var config: ConfigFile? = null

    @BeforeEach
    fun setUp() {
        config = ConfigFile(
                listOf(
                        ConfigIterationArray(
                                name = "ConfigIterationArray1",
                                values = listOf("1", "2")
                        ),
                        ConfigIterationDependent(
                                name = "ConfigIterationDependent",
                                dependentVarName = "ConfigIterationIntRange",
                                modifier = "+1"
                        ),
                        ConfigIterationIntRange(
                                name = "ConfigIterationIntRange",
                                from = 0,
                                to = 20
                        )
                ),
                ConfigEnvironment("", emptyMap()),
                TaskTypeMatlab(
                        "", "", emptyList()
                )
        )
    }

    @Test
    fun testProperConfig() {
        val res = ConfigFileValidator().validate(config!!)
        Assertions.assertEquals(true, res.success)
        Assertions.assertEquals(true, res.messages.isEmpty())
    }
}