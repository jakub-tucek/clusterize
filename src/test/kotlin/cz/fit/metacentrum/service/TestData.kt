package cz.fit.metacentrum.service

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import cz.fit.metacentrum.domain.config.*
import cz.fit.metacentrum.domain.meta.ExecutionMetadata

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
                    )
            ),
            ConfigEnvironment(
                    "./out/metadataStorage/",
                    "./out/storage/",
                    "./src/test/resources/sources",
                    mapOf(),
                    listOf(ConfigEnvironmentDependent(
                            name = "CONFIG_ITERATION_DEPENDENT",
                            dependentVarName = "CONFIG_ITERATION_INT_RANGE",
                            modifier = "+1"
                    ))
            ),
            MatlabTaskType(
                    "main_batch01",
                    listOf(
                            "\$CONFIG_ITERATION_ARRAY",
                            "\$CONFIG_ITERATION_INT_RANGE",
                            "\$CONFIG_ITERATION_DEPENDENT",
                            "useGPU",
                            "yes"
                    )
            )
    )
    val metadata = ExecutionMetadata(
            configFile = config,
            iterationCombinations = listOf(
                    mapOf(
                            "CONFIG_ITERATION_ARRAY" to "1",
                            "CONFIG_ITERATION_INT_RANGE" to "2"
                    )
            ),
            storagePath = Jimfs.newFileSystem(Configuration.unix()).getPath("/storage"),
            metadataStoragePath = Jimfs.newFileSystem(Configuration.unix()).getPath("/metadataStorage"),
            sourcesPath = Jimfs.newFileSystem(Configuration.unix()).getPath("/metadataStorage/sources")
    )
}

