package cz.fit.metacentrum.service.executor

import cz.fit.metacentrum.domain.config.ConfigIterationArray
import cz.fit.metacentrum.domain.config.ConfigIterationIntRange
import cz.fit.metacentrum.service.TestData
import cz.fit.metacentrum.service.executor.submit.IterationExecutor
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author Jakub Tucek
 */
internal class IterationExecutorTest {

    private val executor: IterationExecutor = IterationExecutor()


    @Test
    fun testIterationGeneration() {
        val result = executor.execute(
                TestData.metadata.copy(configFile = TestData.metadata.configFile.copy(iterations = listOf(
                        ConfigIterationIntRange(
                                name = "CONFIG_ITERATION_INT_RANGE1",
                                from = 0,
                                to = 1
                        ),
                        ConfigIterationArray(
                                name = "CONFIG_ITERATION_ARRAY",
                                values = listOf("1", "2")
                        ),
                        ConfigIterationIntRange(
                                name = "CONFIG_ITERATION_INT_RANGE2",
                                from = 1,
                                to = 2
                        )
                ))
                )
        )

        Assertions.assertThat(result.iterationCombinations)
                .isNotNull()
                .hasSize(8)

    }
}