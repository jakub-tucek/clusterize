package cz.fit.metacentrum.service.action.submit.executor

import cz.fit.metacentrum.TestData
import cz.fit.metacentrum.domain.config.ConfigIterationArray
import cz.fit.metacentrum.domain.config.ConfigIterationIntRange
import cz.fit.metacentrum.domain.config.StepOperation
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author Jakub Tucek
 */
internal class IterationExecutorTest {

    private val executor: IterationExecutor = IterationExecutor()


    private val defaultData = TestData.metadata.copy(configFile = TestData.metadata.configFile.copy(iterations = listOf(
            ConfigIterationIntRange(
                    name = "CONFIG_ITERATION_INT_RANGE1",
                    from = 0,
                    to = 1,
                    step = 1,
                    stepOperation = StepOperation.PLUS
            ),
            ConfigIterationArray(
                    name = "CONFIG_ITERATION_ARRAY",
                    values = listOf("1", "2")
            ),
            ConfigIterationIntRange(
                    name = "CONFIG_ITERATION_INT_RANGE2",
                    from = 1,
                    to = 2,
                    step = 1,
                    stepOperation = StepOperation.PLUS
            )
    )))

    @Test
    fun testIterationGeneration() {
        val result = executor.execute(defaultData)

        Assertions.assertThat(result.iterationCombinations)
                .isNotNull
                .hasSize(8)
    }

    @Test
    fun testThatChangingPlusToMinusAreEqual() {
        val result = executor.execute(defaultData)
        val result2 = executor.execute(defaultData.copy(
                configFile = defaultData.configFile.copy(
                        iterations = listOf(
                                defaultData.configFile.iterations[0],
                                defaultData.configFile.iterations[1],
                                ConfigIterationIntRange(
                                        name = "CONFIG_ITERATION_INT_RANGE2",
                                        from = 2,
                                        to = 1,
                                        step = 1,
                                        stepOperation = StepOperation.MINUS
                                )
                        )
                ))
        )

        Assertions.assertThat(result.iterationCombinations)
                .isNotEqualTo(result2.iterationCombinations)
                .containsAll(result2.iterationCombinations)
                .hasSize(result2.iterationCombinations!!.size)
    }

    @Test
    fun testThatDivisionAndMultiplicationWorks() {
        val result = executor.execute(defaultData.copy(
                configFile = defaultData.configFile.copy(
                        iterations = listOf(
                                ConfigIterationIntRange(
                                        name = "CONFIG_ITERATION_INT_RANGE",
                                        from = 10,
                                        to = 5,
                                        step = 2,
                                        stepOperation = StepOperation.DIVIDE
                                ),
                                ConfigIterationIntRange(
                                        name = "CONFIG_ITERATION_INT_RANGE2",
                                        from = 5,
                                        to = 10,
                                        step = 2,
                                        stepOperation = StepOperation.MULTIPLY
                                )
                        )
                ))
        )

        Assertions.assertThat(result.iterationCombinations)
                .hasSize(4)
                .containsExactlyInAnyOrder(
                        mapOf("CONFIG_ITERATION_INT_RANGE" to "10", "CONFIG_ITERATION_INT_RANGE2" to "10"),
                        mapOf("CONFIG_ITERATION_INT_RANGE" to "5", "CONFIG_ITERATION_INT_RANGE2" to "10"),
                        mapOf("CONFIG_ITERATION_INT_RANGE" to "10", "CONFIG_ITERATION_INT_RANGE2" to "5"),
                        mapOf("CONFIG_ITERATION_INT_RANGE" to "5", "CONFIG_ITERATION_INT_RANGE2" to "5")
                )
    }
}