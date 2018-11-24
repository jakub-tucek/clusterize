package cz.fit.metacentrum.service

import cz.fit.metacentrum.domain.*

/**
 *
 * @author Jakub Tucek
 */

internal object TestData {
    val config = ConfigFile(
            listOf(
                    ConfigIterationArray(
                            name = "ConfigIterationArray1",
                            values = listOf("1", "2")
                    ),
                    ConfigIterationIntRange(
                            name = "ConfigIterationIntRange",
                            from = 0,
                            to = 20
                    ),
                    ConfigIterationDependent(
                            name = "ConfigIterationDependent",
                            dependentVarName = "ConfigIterationIntRange",
                            modifier = "+1"
                    )
            ),
            ConfigEnvironment("", emptyMap()),
            MatlabTaskType(
                    "", "", emptyList()
            )
    )
    val metadata = ExecutionMetadata(
            configFile = config
    )
}

