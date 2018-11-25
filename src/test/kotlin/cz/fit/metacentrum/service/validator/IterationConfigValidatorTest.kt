package cz.fit.metacentrum.service.validator


import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.ConfigIterationArray
import cz.fit.metacentrum.domain.config.ConfigIterationDependent
import cz.fit.metacentrum.domain.config.ConfigIterationIntRange
import cz.fit.metacentrum.service.TestData
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


/**
 * @author Jakub Tucek
 */
internal class IterationConfigValidatorTest {

    var config: ConfigFile? = null

    @BeforeEach
    fun setUp() {
        config = TestData.config
                .copy(iterations = listOf(
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
                ))
    }

    @Test
    fun testProperConfig() {
        val res = IterationConfigValidator().validate(config!!)
        Assertions.assertThat(res.success).isTrue()
        Assertions.assertThat(res.messages).isEmpty()
    }

    @Test
    fun testInvalidIterations() {
        val invalidConfig = config!!.copy(iterations = listOf(
                ConfigIterationArray(
                        name = "ConfigIterationArray1",
                        values = emptyList()
                ),
                ConfigIterationDependent(
                        name = "ConfigIterationDependent",
                        dependentVarName = "NOT_KNOWN",
                        modifier = "+1"
                ),
                ConfigIterationIntRange(
                        name = "",
                        from = -1,
                        to = 10
                ),
                ConfigIterationIntRange(
                        name = "ConfigIterationDependent",
                        from = 20,
                        to = 10
                )
        ))
        val res = IterationConfigValidator().validate(invalidConfig)

        Assertions.assertThat(res.success)
        Assertions.assertThat(res.messages)
                .hasSize(6)
                .containsExactlyInAnyOrder(
                        "Some iteration names are not unique: [ConfigIterationDependent x 2]",
                        "ConfigIterationArray array value cannot be empty",
                        "ConfigIterationDependent variable does not exist in other iterations",
                        "Name of config iteration cannot be blank",
                        "ConfigIterationIntRange has invalid range: 20 > 10",
                        "ConfigIterationIntRange has invalid values < 0: -1, 10"
                )
    }
}