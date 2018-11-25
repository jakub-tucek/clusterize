package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.ExecutionMetadata
import cz.fit.metacentrum.domain.config.*

/**
 *
 * @author Jakub Tucek
 */

internal object TestData {
    val config = ConfigFile(
            listOf(
                    ConfigIterationArray(
                            name = "CONFIG_ITERATION_ARRAY",
                            values = listOf("1", "2")
                    ),
                    ConfigIterationIntRange(
                            name = "CONFIG_ITERATION_INT_RANGE",
                            from = 0,
                            to = 20
                    ),
                    ConfigIterationDependent(
                            name = "CONFIG_ITERATION_DEPENDENT",
                            dependentVarName = "CONFIG_ITERATION_INT_RANGE",
                            modifier = "+1"
                    )
            ),
            ConfigEnvironment(
                    "./out/scriptRun",
                    mapOf("USE_GPU_ID" to "gpu", "USE_GPU_VALUE" to "yes")
            ),
            MatlabTaskType(
                    "./input",
                    "main_batch01",
                    listOf(
                            "CONFIG_ITERATION_ARRAY",
                            "CONFIG_ITERATION_INT_RANGE",
                            "CONFIG_ITERATION_DEPENDENT",
                            "USE_GPU_ID",
                            "USE_GPU_VALUE"
                    )
            )
    )
    val metadata = ExecutionMetadata(
            configFile = config
    )
}

