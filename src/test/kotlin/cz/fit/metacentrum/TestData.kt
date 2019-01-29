package cz.fit.metacentrum

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import cz.fit.metacentrum.domain.QueueRecord
import cz.fit.metacentrum.domain.config.*
import cz.fit.metacentrum.domain.meta.*
import java.nio.file.FileSystem

/**
 *
 * @author Jakub Tucek
 */

internal object TestData {

    lateinit var fileSystem: FileSystem
    lateinit var config: ConfigFile
    lateinit var metadata: ExecutionMetadata
    lateinit var executedMetadata: ExecutionMetadata
    lateinit var toRerunMetadata: ExecutionMetadata
    lateinit var queueRecordRunning: QueueRecord

    init {
        initProperties()
    }

    fun initProperties() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix())

        config = ConfigFile(
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
        metadata = ExecutionMetadata(
                configFile = config,
                iterationCombinations = listOf(
                        mapOf(
                                "CONFIG_ITERATION_ARRAY" to "1",
                                "CONFIG_ITERATION_INT_RANGE" to "2"
                        )
                ),
                paths = ExecutionMetadataPath(
                        storagePath = fileSystem.getPath("/storage"),
                        metadataStoragePath = fileSystem.getPath("/metadataStorage"),
                        sourcesPath = fileSystem.getPath("/metadataStorage/sources")
                )
        )

        executedMetadata = metadata.copy(
                submittingUsername = "BigBoy",
                jobs = listOf(
                        ExecutionMetadataJob(
                                jobPath = fileSystem.getPath("/storage/job1"),
                                jobId = 1,
                                jobInfo = createJobInfo("1.pid")
                        ),
                        ExecutionMetadataJob(
                                jobPath = fileSystem.getPath("/storage/job2"),
                                jobId = 2,
                                jobInfo = createJobInfo("2.pid")
                        ),
                        ExecutionMetadataJob(
                                jobPath = fileSystem.getPath("/storage/job3"),
                                jobId = 3,
                                jobInfo = createJobInfo("3.pid")
                        )
                ),
                configFile = metadata.configFile.copy(
                        general = metadata.configFile.general.copy(
                                taskName = "task X"
                        )
                ),
                currentState = ExecutionMetadataState.RUNNING
        )

        toRerunMetadata = executedMetadata.copy(
                jobs = executedMetadata.jobs!!.map { job ->
                    job.copy(jobInfo = job.jobInfo.copy(state = ExecutionMetadataState.INITIAL),
                            jobParent = job)
                }
        )

        queueRecordRunning = QueueRecord("81", "pbsuser", "workq", "oneCPUjob",
                "5736",
                "1",
                "1",
                "1gb",
                "04:00",
                QueueRecord.InternalState.R,
                "00:00",
                QueueRecord.State.RUNNING
        )
    }


    private fun createJobInfo(pid: String, status: Int = 0) = JobInfo(null, null, pid, status, null, null, ExecutionMetadataState.QUEUED)
}

