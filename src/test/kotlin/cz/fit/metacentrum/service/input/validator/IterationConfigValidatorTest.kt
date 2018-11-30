package cz.fit.metacentrum.service.input.validator


import cz.fit.metacentrum.domain.config.ConfigFile
import cz.fit.metacentrum.domain.config.ConfigIterationArray
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
                        name = "ConfigIterationIntRange",
                        values = emptyList()
                ),
                ConfigIterationIntRange(
                        name = "",
                        from = -1,
                        to = 10
                ),
                ConfigIterationIntRange(
                        name = "ConfigIterationIntRange",
                        from = 20,
                        to = 10
                )
        ))
        val res = IterationConfigValidator().validate(invalidConfig)

        Assertions.assertThat(res.success)
        Assertions.assertThat(res.messages)
                .containsExactlyInAnyOrder(
                        "Some iteration names are not unique: [ConfigIterationIntRange x 2]",
                        "ConfigIterationArray array value cannot be empty",
                        "Name of config iteration cannot be blank",
                        "ConfigIterationIntRange has invalid range: 20 > 10",
                        "ConfigIterationIntRange has invalid values < 0: -1, 10"
                )
    }
}