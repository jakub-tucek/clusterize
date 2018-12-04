package cz.fit.metacentrum.service

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import cz.fit.metacentrum.domain.config.*
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataPath

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
            ConfigGeneral(
                    metadataStoragePath = "./out/metadataStorage/",
                    storagePath = "./out/storage/",
                    sourcesPath = "./src/test/resources/sources",
                    variables = mapOf(),
                    dependents = listOf(ConfigGeneralDependent(
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
                    ),
                    emptyList()
            ),
            ConfigResources(profile = "custom")
    )
    val metadata = ExecutionMetadata(
            configFile = config,
            iterationCombinations = listOf(
                    mapOf(
                            "CONFIG_ITERATION_ARRAY" to "1",
                            "CONFIG_ITERATION_INT_RANGE" to "2"
                    )
            ),
            paths = ExecutionMetadataPath(
                    storagePath = Jimfs.newFileSystem(Configuration.unix()).getPath("/storage"),
                    metadataStoragePath = Jimfs.newFileSystem(Configuration.unix()).getPath("/metadataStorage"),
                    sourcesPath = Jimfs.newFileSystem(Configuration.unix()).getPath("/metadataStorage/sources")
            )
    )

    val executedMetadata = metadata.copy(
            submittingUsername = "BigBoy",
            jobs = listOf(
                    ExecutionMetadataJob(
                            jobPath = Jimfs.newFileSystem(Configuration.unix()).getPath("/storage/job1"),
                            jobId = 1,
                            pid = "1.pid"
                    ),
                    ExecutionMetadataJob(
                            jobPath = Jimfs.newFileSystem(Configuration.unix()).getPath("/storage/job2"),
                            jobId = 2,
                            pid = "2.pid"
                    ),
                    ExecutionMetadataJob(
                            jobPath = Jimfs.newFileSystem(Configuration.unix()).getPath("/storage/job3"),
                            jobId = 3,
                            pid = "3.pid"
                    )
            ),
            configFile = metadata.configFile.copy(
                    general = metadata.configFile.general.copy(
                            taskName = "task X"
                    )
            )
    )
}

