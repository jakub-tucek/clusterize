package cz.fit.metacentrum.service

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.config.*
import cz.fit.metacentrum.domain.meta.ExecutionMetadata
import cz.fit.metacentrum.domain.meta.ExecutionMetadataJob
import cz.fit.metacentrum.domain.meta.ExecutionMetadataPath
import cz.fit.metacentrum.domain.meta.JobInfo

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
                            to = 20,
                            step = 1,
                            stepOperation = StepOperation.PLUS
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
                    "main_batch01(\$CONFIG_ITERATION_ARRAY, \$CONFIG_ITERATION_INT_RANGE, \$CONFIG_ITERATION_DEPENDENT, 'userGPU', 'yes')"
            ),
            ConfigResources(
                    profile = ConfigResourceProfile.CUSTOM,
                    details = ConfigResourcesDetails(
                            1,
                            "00:00:01",
                            "1gb",
                            1,
                            scratchLocal = "1gb"
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
                            jobInfo = createJobInfo("1.pid")
                    ),
                    ExecutionMetadataJob(
                            jobPath = Jimfs.newFileSystem(Configuration.unix()).getPath("/storage/job2"),
                            jobId = 2,
                            jobInfo = createJobInfo("2.pid")
                    ),
                    ExecutionMetadataJob(
                            jobPath = Jimfs.newFileSystem(Configuration.unix()).getPath("/storage/job3"),
                            jobId = 3,
                            jobInfo = createJobInfo("3.pid")
                    )
            ),
            configFile = metadata.configFile.copy(
                    general = metadata.configFile.general.copy(
                            taskName = "task X"
                    )
            )
    )

    val queueRecordRunning = QueueRecord("81", "pbsuser", "workq", "oneCPUjob",
            "5736",
            "1",
            "1",
            "1gb",
            "04:00",
            QueueRecord.InternalState.R,
            "00:00",
            QueueRecord.State.RUNNING
    )

    fun createJobInfo(pid: String, status: Int = 0) = JobInfo(null, null, pid, status, null, null)
}

